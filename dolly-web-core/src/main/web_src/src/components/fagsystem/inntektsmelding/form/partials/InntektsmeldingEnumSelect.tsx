import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Formatters from '~/utils/DataFormatter'

interface InntektsmeldingSelect {
	path: string
	label: string
	formikBag: any
}

type Options = {
	label: string
	value: string
	tema?: string
}
enum enumTypes {
	AARSAK_TIL_UTSETTELSE_TYPE = 'Årsak til utsettelse',
	AARSAK_VED_ENDRING_TYPE = 'Årsak ved endring',
	BEGRUNNELSE_TYPE = 'Begrunnelse',
	NATURALYTELSE_TYPE = 'Naturalytelse',
	YTELSE_TYPE = 'Ytelse'
}

const kodeTranslator = (label: string) => {
	switch (label) {
		case enumTypes.AARSAK_TIL_UTSETTELSE_TYPE:
			return 'AARSAK_TIL_UTSETTELSE_TYPE'
		case enumTypes.AARSAK_VED_ENDRING_TYPE:
			return 'AARSAK_VED_ENDRING_TYPE'
		case enumTypes.BEGRUNNELSE_TYPE:
			return 'BEGRUNNELSE_TYPE'
		case enumTypes.NATURALYTELSE_TYPE:
			return 'NATURALYTELSE_TYPE'
		case enumTypes.YTELSE_TYPE:
			return 'YTELSE_TYPE'
	}
}
export default ({ path, label, formikBag }: InntektsmeldingSelect) => {
	const enumtype: string = kodeTranslator(label)
	return (
		<LoadableComponent
			onFetch={() =>
				SelectOptionsOppslag.hentInntektsmeldingOptions(enumtype).then(response =>
					response.map((value: string) => {
						const optionLabel = Formatters.uppercaseAndUnderscoreToCapitalized(value)
							//mer presist å gå lowercase først, æøå og deretter capitalized
							//Sjekk om capitalized funker der det er flere ord
							.replace('oe', 'ø')
							.replace('ae', 'æ')
							.replace('aa', 'å')
						const tema = label === enumTypes.YTELSE_TYPE ? findTema(value) : null
						return { value, label: optionLabel, tema }
					})
				)
			}
			render={(data: Array<Options>) => (
				<DollySelect
					name={path}
					label={label}
					options={data}
					type="text"
					size="xlarge"
					value={_get(formikBag.values, path)}
					onChange={(e: any) =>
						label === enumTypes.YTELSE_TYPE && setYtelseOgTema(e, formikBag, path)
					}
					feil={
						_has(formikBag.touched, path) &&
						_get(formikBag.values, path) === '' && {
							feilmelding: 'Feltet er påkrevd'
						}
					}
					isClearable={false}
				/>
			)}
		/>
	)
}

const findTema = (ytelse: string) => {
	if (['SYKEPENGER'].includes(ytelse)) return 'SYK'
	else if (['FORELDREPENGER', 'SVANGERSKAPSPENGER'].includes(ytelse)) return 'FOR'
	else return 'OMS'
}

const setYtelseOgTema = (value: any, formikBag: any, path: string) => {
	formikBag.setFieldValue('inntektsmelding.joarkMetadata.tema', value.tema)
	formikBag.setFieldValue(path, value.value)
}
