const path = require("path");

module.exports = {
  entry: "./src/index.js",
  output: {
    path: path.resolve(__dirname, "dist"),
    filename: "dolly-assets.js",
    library: {
      name: "dolly-assets",
      type: "umd",
    },
  },
  module: {
    rules: [
      {
        test: /\.png/,
        type: "asset/resource",
      },
      {
        test: /\.svg/,
        type: "asset/resource",
      },
    ],
  },
};
