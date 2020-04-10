# Shopping App /w Jsoup for Android

<img src="https://github.com/cnkay/ShoppingApp/blob/master/images/build.svg"> <img src="https://github.com/cnkay/ShoppingApp/blob/master/images/license.svg"> <img src="https://github.com/cnkay/ShoppingApp/blob/master/images/api.svg">

##### Project has products.json file in assets folder, app scrapes products descriptions from Amazon links with CSS Selectors.(For more information https://www.w3schools.com/cssref/css_selectors.asp)
##### When you drag right product card, app saves the product to initial SQLite database.(assets/products.db)
##### When you drag left product card, app ignores the product and inserts to discarded table.
##### Can see saved products in Saved tab.
##### Can set categories on Category tab
<img src="https://media.giphy.com/media/lRRulQ8Z1R2oQHcf5Q/giphy.gif"> <img src="https://media.giphy.com/media/hVUvvkZrcXiWC6dS9e/giphy.gif"> <img src="https://media.giphy.com/media/Jt56XRHRYA2nwIKuyn/giphy.gif">
<img src="https://media.giphy.com/media/Yn5DMUUNMWOTeKY5Y1/giphy.gif"> <img src="https://media.giphy.com/media/TfLVhCqTYIRX109mDo/giphy.gif">


### build.gradle
```
implementation 'org.jsoup:jsoup:1.12.2'
implementation 'com.github.bumptech.glide:glide:4.11.0'
implementation 'com.google.code.gson:gson:2.8.6'
implementation 'com.google.android.material:material:1.2.0-alpha05'
implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.0.0'
implementation group: 'com.readystatesoftware.sqliteasset', name: 'sqliteassethelper', version: '2.0.1'
implementation 'com.lorentzos.swipecards:library:1.0.9'
annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
```

### products.json
```json
{
    "items": [
      {
        "id": 1,
        "title": "Colgate Kids Toothpaste Pump, Maximum Cavity Protection",
        "subtitle": "4.4 ounces (6 Pack)",
        "price": "$17.76",
        "image": "https://images-na.ssl-images-amazon.com/images/I/91ZEZc-n-CL._SL1500_.jpg",
        "url": "www.amazon.com/Colgate-Maximum-Cavity-Protection-Toothpaste/dp/B07DT7M9BS",
        "category": 4
      },
      {
        "id": 2,
        "title": "Tanner's Tasty Paste Anticavity Fluoride Children’s Toothpaste",
        "subtitle": "Vanilla Bling, 4.2 Ounce",
        "price": "$6.99",
        "image": "https://images-na.ssl-images-amazon.com/images/I/41TDLSQ-n8L._AC_.jpg",
        "url": "https://www.amazon.com/dp/B015ZRTHVA",
        "category": 4
      },
      {
        "..."
      }
    ]
}
```
### ScrapeTask.java
```java
...
 try {
                String URL = productData.getProductURL();
                Document doc = Jsoup.connect(URL).get(); // Jsoup scrapes data from amazon
                Elements description = new Elements();   // For more information https://jsoup.org/
                for (String selector : descriptionCSSselectors) {
                    description = doc.select(selector);
                    if (description.hasText()) {
                        break;
                    }
                }
                String strDescription = description.html();
                if (strDescription.length() > 0) {
                    productData.setDescription(Jsoup.parse(strDescription).text());
                } else
                    productData.setDescription("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
  }
...
```
### ShoppingFragment.java
```java
...
// Use this for debug
// dataList = dbAdapter.findAll();
// Use this for production
dataList = dbAdapter.getProductsForShopping(); // Filtered products (Filtered by discarded,saved and selected category)
...
```

