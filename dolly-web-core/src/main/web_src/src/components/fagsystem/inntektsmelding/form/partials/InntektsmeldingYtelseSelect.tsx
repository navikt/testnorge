import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Formatters from '~/utils/DataFormatter'
import { FormikProps } from 'formik'
import { Ytelser, Tema } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

interface InntektsmeldingSelect {
	path: string
	idx: number
	label: string
	formikBag: FormikProps<{}>
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
}: InntektsmeldingSelect) => (
	<LoadableComponent
		onFetch={() =>
			SelectOptionsOppslag.hentInntektsmeldingOptions(kodeverk).then(response =>
				response.map((value: string) => ({
					value,
					label: Formatters.codeToNorskLabel(value),
					tema: findTema(value)
				}))
			)
		}
		render={(data: Array<Option>) => (
			<DollySelect
				name={path}
				label={label}
				options={data}
				type="text"
				size={size}
				value={_get(formikBag.values, path)}
				onChange={(e: Option) => setYtelseOgTema(e, formikBag, path, idx)}
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

const findTema = (ytelse: string) => {
	if (ytelse === Ytelser.Sykepenger) return Tema.Syk
	else if (ytelse === Ytelser.Foreldrepenger || ytelse === Ytelser.Svangerskapspenger)
		return Tema.For
	else return Tema.Oms
}

const setYtelseOgTema = (value: Option, formikBag: FormikProps<{}>, path: string, idx: number) => {
	const inntektPath = `inntektsmelding.inntekter[${idx}]` //TODO hente hele path!

	formikBag.setFieldValue('inntektsmelding.joarkMetadata.tema', value.tema)

	const {
		sykepengerIArbeidsgiverperioden,
		startdatoForeldrepengeperiode,
		pleiepengerPerioder,
		omsorgspenger,
		...rest
	} = _get(formikBag.values, inntektPath)

	switch (value.value) {
		case Ytelser.Omsorgspenger:
			formikBag.setFieldValue(inntektPath, {
				...rest,
				ytelse: value.value,
				omsorgspenger: { harUtbetaltPliktigeDager: false }
			})
			break
		case Ytelser.Sykepenger:
			formikBag.setFieldValue(inntektPath, {
				...rest,
				ytelse: value.value,
				sykepengerIArbeidsgiverperioden: { bruttoUtbetalt: '' }
			})

			break
		case Ytelser.Foreldrepenger:
			formikBag.setFieldValue(inntektPath, {
				...rest,
				ytelse: value.value,
				startdatoForeldrepengeperiode: ''
			})
			break
		case Ytelser.Pleiepenger:
			formikBag.setFieldValue(inntektPath, {
				...rest,
				ytelse: value.value,
				pleiepengerPerioder: [{ fom: '', tom: '' }]
			})
			break
		default:
			// Foreløpig ingen spesielle keys for opplærings- og svangerskapspenger
			formikBag.setFieldValue(path, value.value)
	}
}
