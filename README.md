# go-api-generator

## What is this?
This project aims to implement a transpilator that receive one pre-defined language, called temporarily being called `apiapi` , and generate one golang API with the defined specifications.

The API use [vestigo](https://github.com/husobee/vestigo) as HTTP router and [gorm](https://gorm.io/index.html) as ORM.

## The language
The grammar of the language can be consulted in the `grammar` file, and one example is presented in `example.apiapi` file.

Basically, the specification needs to have this struct:
```
Models {
	<Model name> {
		<Model field>: <Model type = string, int, float, type>
	}
	# comment #
}

Routes {
	Models.<Model> {
		<Route name> {
			Method: <Route method = GET, POST, PUT, DELETE>
			Path: <Route path>
		}
	}
}
```

For now, only 1:n relationships are accepted.

## Generating one API

The project can be build using `make build` (attempt to set the correct paths inside `Makefile`), and then the API can be generated using:

```bash
make generate SPEC=example.apiapi COMPILE_OUTPUT=output GENERATE_OUTPUT=generated_code.go
```

... where names are self-describing.

After generating, you need to put the generated file in `goproject` folder, named as `main.go`. Then, you can just run `goproject` as you normally would do!

## Example

One generated API can be consulted in `goproject`.

## Tests

Some tests of the lexer, parser and semantic analyzer can be checked in `./tests`.
