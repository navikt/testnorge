import {
	DollyFaBlokk,
	DollyFieldArrayWrapper,
	FieldArrayAddButton,
} from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Arbeidsforhold, Forskudd, Fradrag, Inntekt } from './inntektstubTypes'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FormDateTimepicker } from '@/components/ui/form/inputs/timepicker/Timepicker'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useFieldArray } from 'react-hook-form'

interface InntektendringForm {
	formMethods: UseFormReturn
	path: string
}

type Inntektslister = {
	inntektsliste: Array<Inntekt>
	fradragsliste: Array<Fradrag>
	forskuddstrekksliste: Array<Forskudd>
	arbeidsforholdsliste: Array<Arbeidsforhold>
	rapporteringsdato: string
}

const hjelpetekst = `Den øverste inntektinformasjonen er den gjeldende inntekten. All inntektsinformasjon merket med "Versjon #" er historiske endringer der økende versjonsnummer er nyere.`

export default ({ formMethods, path }: InntektendringForm) => {
	const kopiAvGjeldendeInntekt = formMethods.watch(path)
	const initialValues: Inntektslister = {
		arbeidsforholdsliste: kopiAvGjeldendeInntekt.arbeidsforholdsliste,
		forskuddstrekksliste: kopiAvGjeldendeInntekt.forskuddstrekksliste,
		fradragsliste: kopiAvGjeldendeInntekt.fradragsliste,
		inntektsliste: kopiAvGjeldendeInntekt.inntektsliste,
		rapporteringsdato: kopiAvGjeldendeInntekt.rapporteringsdato,
	}
	const historikkPath = `${path}.historikk`
	const data = formMethods.watch(historikkPath, [])
	const fieldMethods = useFieldArray({ control: formMethods.control, name: historikkPath })

	const handleRapporteringDateChange = (selectedDate: Date, listePath: string) => {
		formMethods.setValue(
			`${listePath}.rapporteringsdato`,
			selectedDate && selectedDate.toISOString().substring(0, 19),
		)
	}

	const addNewEntry = () => fieldMethods.append(initialValues)
	return (
		<ErrorBoundary>
			<DollyFieldArrayWrapper>
				{data.map((_c: Inntektslister, idx: number) => {
					const listePath = `${historikkPath}[${idx}]`
					const clickRemove = () => fieldMethods.remove(idx)
					return (
						<DollyFaBlokk
							key={idx}
							idx={idx}
							hjelpetekst={hjelpetekst}
							header={`Inntektsendring (versjon ${idx + 1})`}
							handleRemove={clickRemove}
						>
							<FormDateTimepicker
								formMethods={formMethods}
								name={`${listePath}.rapporteringsdato`}
								label="Rapporteringsdato"
								onChange={(date: Date) => handleRapporteringDateChange(date, listePath)}
							/>
							<InntektsinformasjonLister formMethods={formMethods} path={listePath} />
						</DollyFaBlokk>
					)
				})}
				<FieldArrayAddButton
					addEntryButtonText="Inntektsendring (historikk)"
					onClick={addNewEntry}
				/>
			</DollyFieldArrayWrapper>
		</ErrorBoundary>
	)
}
