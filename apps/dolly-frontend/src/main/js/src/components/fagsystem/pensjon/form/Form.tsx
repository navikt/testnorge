import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { getAlder, validation } from '@/components/fagsystem/pensjon/form/validation'
import React, { useContext, useState } from 'react'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'
import { ToggleGroup } from '@navikt/ds-react'
import { FyllInnInntektForm } from '@/components/fagsystem/pensjon/form/FyllInnInntektForm'
import { GenerertInntektForm } from '@/components/fagsystem/pensjon/form/GenerertInntektForm'
import {
	initialPensjonGenerertInntekt,
	initialPensjonInntekt,
} from '@/components/fagsystem/pensjon/form/initialValues'

export const pensjonPath = 'pensjonforvalter.inntekt'
export const pensjonGenererPath = 'pensjonforvalter.generertInntekt'

const hjelpetekst =
	'Hvis nedjuster med grunnbeløp er valgt skal beløp angis som årsbeløp i dagens kroneverdi, ' +
	'og vil nedjusteres basert på snitt grunnbeløp i inntektsåret.'

const inputValg = { generertInntekt: 'generer', fyllInnInntekt: 'fyllInn' }

export const PensjonForm = () => {
	const formMethods = useFormContext()
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const [inputType, setInputType] = useState(
		formMethods.getValues(pensjonGenererPath)
			? inputValg.generertInntekt
			: inputValg.fyllInnInntekt,
	)
	const { nyBestilling, nyBestillingFraMal } = opts?.is

	const curDate = new Date()

	const alder = formMethods.watch('pdldata.opprettNyPerson.foedtFoer')
		? curDate.getFullYear() -
			// @ts-ignore
			new Date(formMethods.watch('pdldata.opprettNyPerson.foedtFoer')).getFullYear()
		: getAlder(formMethods.watch(), opts?.personFoerLeggTil, opts?.importPersoner)

	function kalkulerIdentGyldigAlder() {
		const minAlder = alder && (curDate.getFullYear() - alder < 1997 ? 17 : 13)
		return alder && curDate.getFullYear() - alder + minAlder
	}

	const gyldigFraOgMedAar = kalkulerIdentGyldigAlder()

	return (
		<Vis attributt={[pensjonPath, pensjonGenererPath]}>
			<Panel
				heading="Pensjonsgivende inntekt (POPP)"
				hasErrors={panelError(pensjonPath) || panelError(pensjonGenererPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.watch(), [pensjonPath, pensjonGenererPath])}
				informasjonstekst={hjelpetekst}
			>
				{!alder && (nyBestilling || nyBestillingFraMal) && (
					<StyledAlert variant={'info'} size={'small'}>
						Pensjonsgivende inntekt kan settes tidligst fra året personen fyller 13 år. For å sikre
						at personen får gyldig alder kan denne settes ved å huke av for "Alder" på forrige side.
					</StyledAlert>
				)}

				<ToggleGroup
					size={'small'}
					onChange={(value) => {
						const isGenerertInntekt = value === inputValg.generertInntekt
						const activePath = isGenerertInntekt ? pensjonGenererPath : pensjonPath
						const inactivePath = isGenerertInntekt ? pensjonPath : pensjonGenererPath
						const currentInitialValues = isGenerertInntekt
							? initialPensjonGenerertInntekt
							: initialPensjonInntekt

						formMethods.setValue(activePath, currentInitialValues)
						formMethods.setValue(inactivePath, undefined)
						setInputType(value)
					}}
					defaultValue={
						formMethods.getValues(pensjonGenererPath)
							? inputValg.generertInntekt
							: inputValg.fyllInnInntekt
					}
					style={{ margin: '5px 0 5px', backgroundColor: '#ffffff' }}
				>
					<ToggleGroup.Item key={inputValg.fyllInnInntekt} value={inputValg.fyllInnInntekt}>
						Fyll inn inntekt
					</ToggleGroup.Item>
					<ToggleGroup.Item key={inputValg.generertInntekt} value={inputValg.generertInntekt}>
						Generer inntekt
					</ToggleGroup.Item>
				</ToggleGroup>
				{inputType === inputValg.fyllInnInntekt && (
					<FyllInnInntektForm gyldigFraOgMedAar={gyldigFraOgMedAar} formMethods={formMethods} />
				)}
				{inputType === inputValg.generertInntekt && (
					<GenerertInntektForm gyldigFraOgMedAar={gyldigFraOgMedAar} formMethods={formMethods} />
				)}
			</Panel>
		</Vis>
	)
}

PensjonForm.validation = validation
