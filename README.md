# my-stock
自分が保有する投資信託がベンチマークしてる指数を確認できるアプリ

イメージとしては、iphoneの株価アプリ

## 構成

```
client -> akka-http(route) -> 親akka-actor -> 子akka-actor -> リポジトリ(外部api叩く)
```

上記の構成では、actorを使うメリットないので、以下のような構成にしてみる

```
client → akka-http(route) → 親akka-actor → 子akka-actor（外部api叩く用） → defaults.usecase -> リポジトリ(外部api叩く)
　                                       ↓
                                         → 子akka-actor（なんか処理する用） → defaults.usecase
```                                  
                                           
### クライアント
- PWA(Vue.js)

### サーバー
- akka-http（apiとして）

#### 利用するライブラリ
- akka-actor
- akka-http
- cats
- yahoo-api-client <- 自前で作る。作成中

### 外部api
- [quandl](https://www.quandl.com/tools/full-list)

世界中の株式・指数（日経平均など）が取得できるapiらしい

↑過去の数値しか取れないので、使いにくい。

なので、↓に変更

- [rapidapi](https://rapidapi.com/)

rapidapiからYahoo Finance APIを叩いて、株価情報を取得する
