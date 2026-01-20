import {
	DollyFaBlokk,
	DollyFieldArrayWrapper,
	FieldArrayAddButton,
} from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Forskudd, Fradrag, Inntekt } from './inntektstubTypes'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useFieldArray } from 'react-hook-form'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

interface InntektendringForm {
	formMethods: UseFormReturn
	path: string
}

type Inntektslister = {
	inntektsliste: Array<Inntekt>
	fradragsliste: Array<Fradrag>
	forskuddstrekksliste: Array<Forskudd>
	rapporteringsdato: string
}

const hjelpetekst = `Den øverste inntektinformasjonen er den gjeldende inntekten. All inntektsinformasjon merket med "Versjon #" er historiske endringer der økende versjonsnummer er nyere.`

export default ({ formMethods, path }: InntektendringForm) => {
	const kopiAvGjeldendeInntekt = formMethods.watch(path)
	const initialValues: Inntektslister = {
		forskuddstrekksliste: kopiAvGjeldendeInntekt.forskuddstrekksliste,
		fradragsliste: kopiAvGjeldendeInntekt.fradragsliste,
		inntektsliste: kopiAvGjeldendeInntekt.inntektsliste,
		rapporteringsdato: kopiAvGjeldendeInntekt.rapporteringsdato,
	}
	const historikkPath = `${path}.historikk`
	const data = formMethods.watch(historikkPath, [])
	const fieldMethods = useFieldArray({ control: formMethods.control, name: historikkPath })

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
							<FormDatepicker
								format={'DD.MM.YYYY HH:mm'}
								name={`${listePath}.rapporteringsdato`}
								label="Rapporteringsdato"
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
