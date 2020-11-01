# my-stock
自分が保有する投資信託がベンチマークしてる指数を確認できるアプリ

## 構成

client -> akka-http(route) -> 親akka-actor -> 子akka-actor -> リポジトリ(外部api叩く)

### クライアント
- PWA(Vue.js)

### サーバー
- akka-http（apiとして）

#### 利用するライブラリ
- akka-http
- cats

### 外部api
[quandl](https://www.quandl.com/tools/full-list)
↑世界中の株式・指数（日経平均など）が取得できるapiらしい

