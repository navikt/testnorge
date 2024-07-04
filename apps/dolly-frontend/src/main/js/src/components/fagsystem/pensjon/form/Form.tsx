import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from '@/components/fagsystem/pensjon/form/validation'
import React, { useContext, useState } from 'react'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'
import { ToggleGroup } from '@navikt/ds-react'
import {
	initialPensjonGenererInntekt,
	initialPensjonInntekt,
} from '@/components/fagsystem/aareg/form/initialValues'
import { GenererInntektForm } from '@/components/fagsystem/pensjon/form/GenererInntektForm'
import { FyllInnInntektForm } from '@/components/fagsystem/pensjon/form/FyllInnInntektForm'

export const pensjonPath = 'pensjonforvalter.inntekt'
export const pensjonGenererPath = 'pensjonforvalter.genererInntekt'

const hjelpetekst =
	'Hvis nedjuster med grunnbeløp er valgt skal beløp angis som årsbeløp i dagens kroneverdi, ' +
	'og vil nedjusteres basert på snitt grunnbeløp i inntektsåret.'

const inputValg = { genererInntekt: 'generer', fyllInnInntekt: 'fyllInn' }

export const PensjonForm = () => {
	const formMethods = useFormContext()
	const opts = useContext(BestillingsveilederContext)
	const [inputType, setInputType] = useState(
		formMethods.getValues(pensjonGenererPath) ? inputValg.genererInntekt : inputValg.fyllInnInntekt,
	)
	const { nyBestilling, nyBestillingFraMal } = opts?.is

	function kalkulerIdentFyltSyttenAar() {
		const curDate = new Date()
		const alder =
			formMethods.watch('pdldata.opprettNyPerson.foedtFoer') &&
			formMethods.watch('pdldata.opprettNyPerson.foedtFoer') !== null
				? curDate.getFullYear() -
					// @ts-ignore
					new Date(formMethods.watch('pdldata.opprettNyPerson.foedtFoer')).getFullYear()
				: formMethods.watch('pdldata.opprettNyPerson.alder')
		return alder && curDate.getFullYear() - alder + 17
	}

	const syttenFraOgMedAar = kalkulerIdentFyltSyttenAar()
	const minAar = new Date().getFullYear() - 17
	const valgtAar =
		formMethods.watch(`${pensjonPath}.fomAar`) || formMethods.watch(`${pensjonGenererPath}.fomAar`)

	return (
		<Vis attributt={[pensjonPath, pensjonGenererPath]}>
			<Panel
				heading="Pensjonsgivende inntekt (POPP)"
				hasErrors={panelError(pensjonPath) || panelError(pensjonGenererPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [pensjonPath, pensjonGenererPath])}
				informasjonstekst={hjelpetekst}
			>
				{!formMethods.getValues('pdldata.opprettNyPerson.alder') &&
					valgtAar < minAar &&
					(nyBestilling || nyBestillingFraMal) && (
						<StyledAlert variant={'info'} size={'small'}>
							Pensjonsgivende inntekt kan settes fra året personen fyller 17 år. For å sikre at
							personen får gyldig alder kan denne settes ved å huke av for "Alder" på forrige side.
						</StyledAlert>
					)}

				<ToggleGroup
					size={'small'}
					onChange={(value) => {
						const isGenererInntekt = value === inputValg.genererInntekt
						const activePath = isGenererInntekt ? pensjonGenererPath : pensjonPath
						const inactivePath = isGenererInntekt ? pensjonPath : pensjonGenererPath
						const currentInitialValues = isGenererInntekt
							? initialPensjonGenererInntekt
							: initialPensjonInntekt

						formMethods.setValue(activePath, currentInitialValues)
						formMethods.setValue(inactivePath, undefined)
						setInputType(value)
					}}
					defaultValue={
						formMethods.getValues(pensjonGenererPath)
							? inputValg.genererInntekt
							: inputValg.fyllInnInntekt
					}
					style={{ margin: '5px 0 5px', backgroundColor: '#ffffff' }}
				>
					<ToggleGroup.Item key={inputValg.fyllInnInntekt} value={inputValg.fyllInnInntekt}>
						Fyll inn inntekt
					</ToggleGroup.Item>
					<ToggleGroup.Item key={inputValg.genererInntekt} value={inputValg.genererInntekt}>
						Generer inntekt
					</ToggleGroup.Item>
				</ToggleGroup>
				{inputType === inputValg.genererInntekt && (
					<GenererInntektForm syttenFraOgMedAar={syttenFraOgMedAar} formMethods={formMethods} />
				)}
				{inputType === inputValg.fyllInnInntekt && (
					<FyllInnInntektForm syttenFraOgMedAar={syttenFraOgMedAar} />
				)}
			</Panel>
		</Vis>
	)
}

PensjonForm.validation = validation
