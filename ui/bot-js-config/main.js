import {Telegraf, Markup} from 'telegraf'
const token = '7600007090:AAFiOYTfEIR2TYHU_jB1QQ5qWOlgW7ac1Zs'
const webAppUrl = 'https://www.instagram.com/rybakofficial/?locale=ru&hl=he'

const bot = new Telegraf(token)

bot.command('start', (ctx) => {
  ctx.reply('Добро пожаловать, нажми на кнопку, чтобы запустить приложение',
    Markup.keyboard([Markup.button.webApp('Отправить сообщение', webAppUrl)]),
  )
})
bot.launch()
