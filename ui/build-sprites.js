'use strict';

const SVGSpriter = require('svg-sprite');
const path = require('path');
const mkdirp = require('mkdirp');
const fs = require('fs');

const svgDirectoryPath = 'src/assets/svg-for-sprites/';
const iconIds = [];
const config = {
  dest: 'src/assets/sprites',
  shape: {
    id: {
      generator: (filePath) => {
        // для генерации id картинок используем название файла
        const iconId = filePath.slice(svgDirectoryPath.length, -4);
        iconIds.push(iconId);
        return iconId;
      },
    },
  },
  mode: {
    symbol: {
      bust: true,
      // если раскоментить example: true то в той же директории что и файл со спрайтами сгенерируется html файл для просмотра всех картинок в спрайте
      //example: true,
      sprite: 'svg-sprite.svg',
    },
  },
};
const spriter = new SVGSpriter(config);
fs.readdir(svgDirectoryPath, (err, files) => {
  // перебираем папку
  files.forEach((fileName) => {
    const relativePath = svgDirectoryPath + fileName;
    // костылим для чтобы слеш в пути файла определился сам т.к. spriter проверяет разрезолвленный путь с тем что передали во втором аргументе
    const filePath = path.resolve(relativePath).slice(-relativePath.length);
    if (fs.lstatSync(filePath).isFile() && fileName.endsWith('.svg')) {
      spriter.add(path.resolve(filePath), filePath, fs.readFileSync(path.resolve(filePath), { encoding: 'utf-8' }));
    }
  });
  // Компилим файлы
  spriter.compile(function (error, result) {
    const hash = {};
    for (let mode in result) {
      for (let type in result[mode]) {
        mkdirp.sync(path.dirname(result[mode][type].path));
        const filePath = result[mode][type].path;
        const postfixSvgFile = '.svg';
        if (filePath.endsWith(postfixSvgFile)) {
          // файл svg в каждом режиме генерится один поэтому просто парсим вот так добавленный хеш, подругому его не вынуть я смотрел исходиники
          hash[mode] = filePath.slice(filePath.lastIndexOf('-') + 1, filePath.length - postfixSvgFile.length);
        }
        fs.writeFileSync(filePath, result[mode][type].contents);
      }
    }
    // if (!hash.symbol) {
    //   throw Error('На всякий случай упадем если symbol нет');
    // }
    console.log('sprites-hash', hash);
    // сохранеям хеш в файл
    fs.writeFileSync(
      path.resolve('src/sprites-hash.ts'),
      'export const spritesHash = {' +
      Object.entries(hash)
        .map(([key, value]) => key + ':"' + value + '"')
        .join(',') +
      '}',
    );
    // создаем файл типа для идентификатора
    fs.writeFileSync(
      path.resolve('src/sprites-ids.type.ts'),
      'export type SpriteIdsType = ' + iconIds.map((id) => `'${id}'`).join(' | '),
    );
  });
});
