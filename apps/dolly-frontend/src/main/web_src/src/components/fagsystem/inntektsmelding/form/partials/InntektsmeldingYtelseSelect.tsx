import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import LoadableComponent, { Feilmelding } from '~/components/ui/loading/LoadableComponent'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Formatters from '~/utils/DataFormatter'
import { FormikProps } from 'formik'
import { Tema, Ytelser } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

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
}: InntektsmeldingSelect) => {
	const ytelsePath = `${path}.ytelse`

	const feil = (feilmelding: Feilmelding) => {
		if (_has(formikBag.touched, ytelsePath) && _get(formikBag.values, ytelsePath) === '')
			return { feilmelding: 'Feltet er påkrevd' }
		else return feilmelding
	}

	return (
		<ErrorBoundary>
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
				render={(data: Array<Option>, feilmelding: Feilmelding) => (
					<DollySelect
						name={ytelsePath}
						label={label}
						options={data}
						type="text"
						size={size}
						value={_get(formikBag.values, ytelsePath)}
						onChange={(e: Option) => setYtelseOgTema(e, formikBag, path, idx)}
						feil={feil(feilmelding)}
						isClearable={false}
					/>
				)}
			/>
		</ErrorBoundary>
	)
}

const findTema = (ytelse: string) => {
	if (ytelse === Ytelser.Sykepenger) return Tema.Syk
	else if (ytelse === Ytelser.Foreldrepenger || ytelse === Ytelser.Svangerskapspenger)
		return Tema.For
	else return Tema.Oms
}

const setYtelseOgTema = (value: Option, formikBag: FormikProps<{}>, path: string, idx: number) => {
	formikBag.setFieldValue('inntektsmelding.joarkMetadata.tema', value.tema)

	const {
		sykepengerIArbeidsgiverperioden,
		startdatoForeldrepengeperiode,
		pleiepengerPerioder,
		omsorgspenger,
		...rest
	} = _get(formikBag.values, path)

	if (value.value === Ytelser.Omsorgspenger) {
		formikBag.setFieldValue(path, {
			...rest,
			ytelse: value.value,
			omsorgspenger: { harUtbetaltPliktigeDager: false },
			sykepengerIArbeidsgiverperioden: { bruttoUtbetalt: '' }
		})
	} else if (value.value === Ytelser.Sykepenger) {
		formikBag.setFieldValue(path, {
			...rest,
			ytelse: value.value,
			sykepengerIArbeidsgiverperioden: { bruttoUtbetalt: '' }
		})
	} else if (value.value === Ytelser.Foreldrepenger) {
		formikBag.setFieldValue(path, {
			...rest,
			ytelse: value.value,
			startdatoForeldrepengeperiode: ''
		})
	} else if (
		value.value === Ytelser.Pleiepenger ||
		value.value === Ytelser.PleiepengerBarn ||
		value.value === Ytelser.PleiepengerNaerstaaende
	) {
		formikBag.setFieldValue(path, {
			...rest,
			ytelse: value.value,
			pleiepengerPerioder: [{ fom: '', tom: '' }]
		})
	} else {
		// Foreløpig ingen spesielle keys for opplærings- og svangerskapspenger
		formikBag.setFieldValue(`${path}.ytelse`, value.value)
	}
}
