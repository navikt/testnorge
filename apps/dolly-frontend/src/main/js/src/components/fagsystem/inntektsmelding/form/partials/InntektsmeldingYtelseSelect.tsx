import * as _ from 'lodash'
import LoadableComponent, { Feilmelding } from '@/components/ui/loading/LoadableComponent'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { Option, SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { codeToNorskLabel } from '@/utils/DataFormatter'
import { Tema, Ytelser } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface InntektsmeldingSelect {
	path: string
	idx: number
	label: string
	formMethods: UseFormReturn
	kodeverk: string
	size?: string
}

export default ({
	path,
	idx,
	label,
	kodeverk,
	formMethods,
	size = 'medium',
}: InntektsmeldingSelect) => {
	const ytelsePath = `${path}.ytelse`

	return (
		<ErrorBoundary>
			<LoadableComponent
				onFetch={() =>
					SelectOptionsOppslag.hentInntektsmeldingOptions(kodeverk).then((response) =>
						response.map((value: string) => ({
							value,
							label: codeToNorskLabel(value),
							tema: findTema(value),
						})),
					)
				}
				render={(data: Array<Option>, _feilmelding: Feilmelding) => (
					<DollySelect
						name={ytelsePath}
						label={label}
						options={data}
						size={size}
						value={_.get(formMethods.getValues(), ytelsePath)}
						onChange={(e: Option) => {
							setYtelseOgTema(e, formMethods, path, idx)
						}}
						isClearable={false}
					/>
				)}
			/>
		</ErrorBoundary>
	)
}

const findTema = (ytelse: string) => {
	if (ytelse === Ytelser.Sykepenger) {
		return Tema.Syk
	} else if (ytelse === Ytelser.Foreldrepenger || ytelse === Ytelser.Svangerskapspenger) {
		return Tema.For
	} else {
		return Tema.Oms
	}
}

const setYtelseOgTema = (value: Option, formMethods: UseFormReturn, path: string, _idx: number) => {
	formMethods.setValue('inntektsmelding.joarkMetadata.tema', value.tema)

	const {
		sykepengerIArbeidsgiverperioden,
		startdatoForeldrepengeperiode,
		pleiepengerPerioder,
		omsorgspenger,
		...rest
	} = _.get(formMethods.getValues(), path)

	if (value.value === Ytelser.Omsorgspenger) {
		formMethods.setValue(path, {
			...rest,
			ytelse: value.value,
			omsorgspenger: { harUtbetaltPliktigeDager: false },
			sykepengerIArbeidsgiverperioden: { bruttoUtbetalt: '' },
		})
	} else if (value.value === Ytelser.Sykepenger) {
		formMethods.setValue(path, {
			...rest,
			ytelse: value.value,
			sykepengerIArbeidsgiverperioden: { bruttoUtbetalt: '' },
		})
	} else if (value.value === Ytelser.Foreldrepenger) {
		formMethods.setValue(path, {
			...rest,
			ytelse: value.value,
			startdatoForeldrepengeperiode: '',
		})
	} else if (
		value.value === Ytelser.Pleiepenger ||
		value.value === Ytelser.PleiepengerBarn ||
		value.value === Ytelser.PleiepengerNaerstaaende
	) {
		formMethods.setValue(path, {
			...rest,
			ytelse: value.value,
			pleiepengerPerioder: [{ fom: '', tom: '' }],
		})
	} else {
		// Foreløpig ingen spesielle keys for opplærings- og svangerskapspenger
		formMethods.setValue(`${path}.ytelse`, value.value)
	}
	formMethods.trigger(path)
}
