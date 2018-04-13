const rewireReactHotLoader = require('react-app-rewire-hot-loader');

/* config-overrides.js */
module.exports = function override(config, env) {
    config = rewireReactHotLoader(config, env);
    config.entry.unshift('babel-polyfill');

    // with loaderOptions
    // config = rewireLess.withLoaderOptions(someLoaderOptions)(config, env);
    return config;
}