import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Formatters from '~/utils/DataFormatter'
import { FormikProps } from 'formik'

interface InntektsmeldingSelect {
	path: string
	idx?: number
	label: string
	formikBag?: FormikProps<{}>
	kodeverk: string
	size?: string
}

type Option = {
	label: string
	value: string
	tema?: string
}

export default ({
	path,
	idx,
	label,
	kodeverk,
	formikBag,
	size = 'medium'
}: InntektsmeldingSelect) => {
	//TODO: Vurdere å dele opp i ytelse og resten

	const ytelse: boolean = kodeverk === 'YTELSE_TYPE'
	return (
		<LoadableComponent
			onFetch={() =>
				SelectOptionsOppslag.hentInntektsmeldingOptions(kodeverk).then(response =>
					response.map((value: string) => {
						const optionLabel = Formatters.uppercaseAndUnderscoreToCapitalized(value)
							//mer presist å gå lowercase først, æøå og deretter capitalized
							//Sjekk om capitalized funker der det er flere ord
							.replace('oe', 'ø')
							.replace('ae', 'æ')
							.replace('aa', 'å')
						const tema = ytelse ? findTema(value) : null
						return { value, label: optionLabel, tema }
					})
				)
			}
			render={(data: Array<Option>) =>
				ytelse ? (
					<DollySelect
						name={path}
						label={label}
						options={data}
						type="text"
						size={size}
						value={_get(formikBag.values, path)}
						onChange={(e: Option) => ytelse && setYtelseOgTema(e, formikBag, path, idx)}
						feil={
							_has(formikBag.touched, path) &&
							_get(formikBag.values, path) === '' && {
								feilmelding: 'Feltet er påkrevd'
							}
						}
						isClearable={false}
					/>
				) : (
					<FormikSelect
						name={path}
						label={label}
						options={data}
						type="text"
						size={size}
						isClearable={false}
					/>
				)
			}
		/>
	)
}

const findTema = (ytelse: string) => {
	if (['SYKEPENGER'].includes(ytelse)) return 'SYK'
	else if (['FORELDREPENGER', 'SVANGERSKAPSPENGER'].includes(ytelse)) return 'FOR'
	else return 'OMS'
}

const setYtelseOgTema = (value: Option, formikBag: FormikProps<{}>, path: string, idx: number) => {
	const inntektPath = `inntektsmelding.inntekter[${idx}]` //TODO hente hele path!

	formikBag.setFieldValue('inntektsmelding.joarkMetadata.tema', value.tema)
	formikBag.setFieldValue(path, value.value)

	//TODO: Fjerne de andre hvis bruker ombestemmer seg!
	switch (value.value) {
		case 'OMSORGSPENGER':
			formikBag.setFieldValue(`${inntektPath}.omsorgspenger`, { harUtbetaltPliktigeDager: false })
			break
		case 'SYKEPENGER':
			formikBag.setFieldValue(`${inntektPath}.sykepengerIArbeidsgiverperioden`, {
				bruttoUtbetalt: ''
			})
			break
		case 'FORELDREPENGER':
			formikBag.setFieldValue(`${inntektPath}.startdatoForeldrepengeperiode`, '')
			break
	}
}
