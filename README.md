# Android Universal Adapter

 [ ![Download](https://api.bintray.com/packages/lliepmah/com.github.lliepmah/universal-adapter-compiler/images/download.svg) ](https://bintray.com/lliepmah/com.github.lliepmah/universal-adapter-compiler/_latestVersion) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-AndroidUniversalAdapter-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5975)

[![Apache License](https://img.shields.io/badge/license-Apache%20v2-green.svg)](https://github.com/lliepmah/AndroidUniversalAdapter/blob/master/LICENSE) [![Build Status](https://travis-ci.org/lliepmah/AndroidUniversalAdapter.svg?branch=master)](https://travis-ci.org/lliepmah/AndroidUniversalAdapter) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/6728f8d9467b4756be1e1b615023b7ba)](https://www.codacy.com/app/lliepmah/AndroidUniversalAdapter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lliepmah/AndroidUniversalAdapter&amp;utm_campaign=Badge_Grade) [![Coverage Status](https://coveralls.io/repos/github/lliepmah/AndroidUniversalAdapter/badge.svg?branch=master)](https://coveralls.io/github/lliepmah/AndroidUniversalAdapter?branch=master)


## Integration

1) Implement your view holder, by extending `DefaultViewHolder<SomeModelClass>`
2) Annotate it by `@HolderBuilder(R.layout.some_layout)` annotation
3) Library generates view holder builder
4) You can use it with your custom adapter, or with `UniversalAdapter`

### Example

View holder builder implementation:
```java
@HolderBuilder(R.layout.li_label)
class LabelHolder extends DefaultViewHolder<String> {

    private final String mValue;

    @BindView(R.id.li_label_tv_text)
    TextView mTvText;

    @HolderConstructor
    LabelHolder(View view, String value) {
        super(view);
        ButterKnife.bind(this, view);
        mValue = value;
    }

    public LabelHolder(View itemView) {
        super(itemView);
        mValue = "";
    }

    @Override
    public void bind(String text) {
        mTvText.setText(mValue + text);
    }
}
```

Usage holder builder:
```java
  UniversalAdapter adapter = new UniversalAdapter(new ScreenHolderBuilder(this),
                new LabelHolderBuilder("example "));

  adapter.add(getString(R.string.menu));
  adapter.addAll(getScreens());
  
  mRecyclerView.setAdapter(adapter);
  
```

## Gradle

```java
    def universalAdapterVersion = 'x.x.x'
    ...
    dependencies {
      provided "com.github.lliepmah:annotations:$universalAdapterVersion"
      compile "com.github.lliepmah:library:$universalAdapterVersion"
      annotationProcessor "com.github.lliepmah:compiler:$universalAdapterVersion"
    ...
    }
```    
### Kotlin
```java
    def universalAdapterVersion = 'x.x.x'
    ...
    dependencies {
      provided "com.github.lliepmah:annotations:$universalAdapterVersion"
      compile "com.github.lliepmah:library:$universalAdapterVersion"
      kapt "com.github.lliepmah:compiler:$universalAdapterVersion"
    ...
    }
```    
## ProGuard
No special ProGuard rules required.

License
-------

    Copyright 2016 Arthur Korchagin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.