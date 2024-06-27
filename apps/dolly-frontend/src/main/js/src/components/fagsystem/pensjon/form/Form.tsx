import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from '@/components/fagsystem/pensjon/form/validation'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { DollyTextInput, FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { BaseSyntheticEvent, useContext, useEffect, useState } from 'react'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import _ from 'lodash'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'
import { usePensjonFacadeGjennomsnitt } from '@/utils/hooks/usePensjonFacade'
import DollyKjede from '@/components/dollyKjede/DollyKjede'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialPensjonInntekt } from '@/components/fagsystem/aareg/form/initialValues'
import { KjedeContainer } from '@/components/fagsystem/aareg/form/partials/ameldingForm'

export const pensjonPath = 'pensjonforvalter.inntekt'

const hjelpetekst =
	'Hvis nedjuster med grunnbeløp er valgt skal beløp angis som årsbeløp i dagens kroneverdi, ' +
	'og vil nedjusteres basert på snitt grunnbeløp i inntektsåret.'

export const PensjonForm = () => {
	const formMethods = useFormContext()
	const opts = useContext(BestillingsveilederContext)
	const { nyBestilling, nyBestillingFraMal } = opts?.is
	const { pensjon, mutate } = usePensjonFacadeGjennomsnitt(
		formMethods.watch(`${pensjonPath}.generer`),
	)
	const [selectedIndex, setSelectedIndex] = useState(0)

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

	const handleGenererChange = () => {
		formMethods.trigger(pensjonPath)
		mutate()
	}

	useEffect(() => {
		if (pensjon) {
			formMethods.setValue(`${pensjonPath}.inntekter`, pensjon.arInntektGList)
		}
	}, [pensjon])

	const syttenFraOgMedAar = kalkulerIdentFyltSyttenAar()
	const minAar = new Date().getFullYear() - 17
	const valgtAar = formMethods.watch(`${pensjonPath}.fomAar`)

	console.log('pensjon: ', pensjon) //TODO - SLETT MEG

	return (
		<Vis attributt={pensjonPath}>
			<Panel
				heading="Pensjonsgivende inntekt (POPP)"
				hasErrors={panelError(pensjonPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [pensjonPath])}
				informasjonstekst={hjelpetekst}
			>
				{/*// @ts-ignore*/}
				{!_.has(formMethods.getValues(), 'pdldata.opprettNyPerson.alder') &&
					valgtAar < minAar &&
					(nyBestilling || nyBestillingFraMal) && (
						<StyledAlert variant={'info'} size={'small'}>
							Pensjonsgivende inntekt kan settes fra året personen fyller 17 år. For å sikre at
							personen får gyldig alder kan denne settes ved å huke av for "Alder" på forrige side.
						</StyledAlert>
					)}
				<Kategori title="Generert skjema inntekt" vis={pensjonPath}>
					<div className="flexbox--flex-wrap">
						<FormSelect
							name={`${pensjonPath}.generer.fomAar`}
							label="Fra og med år"
							afterChange={handleGenererChange}
							options={getYearRangeOptions(syttenFraOgMedAar || 1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<FormSelect
							name={`${pensjonPath}.generer.tomAar`}
							label="Til og med år"
							afterChange={handleGenererChange}
							options={getYearRangeOptions(1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<FormTextInput
							name={`${pensjonPath}.generer.averageG`}
							useOnChange={true}
							onChange={(event: BaseSyntheticEvent) => {
								formMethods.setValue(`${pensjonPath}.generer.averageG`, event?.target?.value)
								handleGenererChange()
							}}
							label="Gjenomsnitt G"
							type="number"
						/>

						<FormCheckbox
							name={`${pensjonPath}.generer.tillatInntektUnder1G`}
							label="Nedjuster med grunnbeløp"
							size="small"
							checkboxMargin
						/>

						{pensjon?.arInntektGList?.length > 0 && (
							<>
								<KjedeContainer>
									<DollyKjede
										objectList={pensjon?.arInntektGList}
										itemLimit={10}
										selectedIndex={selectedIndex}
										setSelectedIndex={setSelectedIndex}
									/>
									<Hjelpetekst>Kapplah!</Hjelpetekst>
								</KjedeContainer>
								<FormDollyFieldArray
									name={`pensjonforvalter.inntekt.inntekter`}
									header="Inntekt"
									newEntry={initialPensjonInntekt}
									canBeEmpty={false}
								>
									{(path: string, idx: number) => {
										console.log('path: ', path) //TODO - SLETT MEG
										return (
											<div className="flexbox--flex-wrap sigrun-form" key={idx}>
												<DollyTextInput
													name={`${path}.inntekt`}
													label="Inntekt"
													type="number"
													size="large"
												/>
												<DollyTextInput
													isDisabled={true}
													name={`${path}.ar`}
													label="År"
													type="number"
													size="large"
												/>
											</div>
										)
									}}
								</FormDollyFieldArray>
							</>
						)}
					</div>
				</Kategori>
				<Kategori title="Pensjonsgivende inntekt" vis={pensjonPath}>
					<div className="flexbox--flex-wrap">
						<FormSelect
							name={`${pensjonPath}.fomAar`}
							label="Fra og med år"
							options={getYearRangeOptions(syttenFraOgMedAar || 1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<FormSelect
							name={`${pensjonPath}.tomAar`}
							label="Til og med år"
							options={getYearRangeOptions(1968, new Date().getFullYear() - 1)}
							isClearable={false}
						/>

						<FormTextInput name={`${pensjonPath}.belop`} label="Beløp" type="number" />

						<FormCheckbox
							name={`${pensjonPath}.redusertMedGrunnbelop`}
							label="Nedjuster med grunnbeløp"
							size="small"
							checkboxMargin
						/>
					</div>
				</Kategori>
			</Panel>
		</Vis>
	)
}

PensjonForm.validation = validation
