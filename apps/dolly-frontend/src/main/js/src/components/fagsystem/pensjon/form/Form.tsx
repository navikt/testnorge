import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from '@/components/fagsystem/pensjon/form/validation'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import React, { BaseSyntheticEvent, useContext, useEffect, useState } from 'react'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import _ from 'lodash'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'
import { usePensjonFacadeGjennomsnitt } from '@/utils/hooks/usePensjonFacade'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import NavButton from '@/components/ui/button/NavButton/NavButton'

export const pensjonPath = 'pensjonforvalter.inntekt'

const hjelpetekst =
	'Hvis nedjuster med grunnbeløp er valgt skal beløp angis som årsbeløp i dagens kroneverdi, ' +
	'og vil nedjusteres basert på snitt grunnbeløp i inntektsåret.'

const inputValg = { genererInntekt: 'generer', fyllInnInntekt: 'fyllInn' }

const getTittel = (data) => {
	const inntektsaar = data?.map((inntekt) => inntekt.ar)
	const foerste = Math.min(...inntektsaar)
	const siste = Math.max(...inntektsaar)
	return `Genererte inntekter (${foerste} - ${siste})`
}

const Button = styled(NavButton)`
	margin: 10px 10px 10px 2px;
`

const StyledPanel = styled.div`
	width: 790px;
`

export const PensjonForm = () => {
	const formMethods = useFormContext()
	const opts = useContext(BestillingsveilederContext)
	const [inputType, setInputType] = useState(inputValg.fyllInnInntekt)
	const [hentNyeVerdier, setHentNyeVerdier] = useState<boolean>(false)
	const { nyBestilling, nyBestillingFraMal } = opts?.is
	const { pensjon, mutate } = usePensjonFacadeGjennomsnitt(
		formMethods.watch(`${pensjonPath}.generer`),
		hentNyeVerdier,
	)

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
		setHentNyeVerdier(true)
		formMethods.setValue(`${pensjonPath}.inntekter`, [])
	}

	useEffect(() => {
		if (pensjon) {
			console.log('pensjon: ', pensjon) //TODO - SLETT MEG
			formMethods.setValue(`${pensjonPath}.inntekter`, pensjon.arInntektGList)
			setHentNyeVerdier(false)
		}
	}, [pensjon])

	const syttenFraOgMedAar = kalkulerIdentFyltSyttenAar()
	const minAar = new Date().getFullYear() - 17
	const valgtAar = formMethods.watch(`${pensjonPath}.fomAar`)
	const genererteInntekter = formMethods.watch(`${pensjonPath}.inntekter`)

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

				<ToggleGroup
					size={'small'}
					onChange={setInputType}
					defaultValue={inputValg.fyllInnInntekt}
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
					<Kategori
						hjelpetekst={
							'Generer inntekt for hvert år i perioden. Inntekten vil bli generert basert på G-verdi ' +
							'og hver inntekt kan deretter endres manuelt.'
						}
						title="Generert skjema inntekt"
						vis={pensjonPath}
					>
						<div className="flexbox--flex-wrap">
							<FormSelect
								name={`${pensjonPath}.generer.fomAar`}
								label="Fra og med år"
								options={getYearRangeOptions(
									syttenFraOgMedAar || 1968,
									new Date().getFullYear() - 1,
								)}
								size={'xsmall'}
								isClearable={false}
							/>

							<FormSelect
								name={`${pensjonPath}.generer.tomAar`}
								label="Til og med år"
								options={getYearRangeOptions(1968, new Date().getFullYear() - 1)}
								size={'xsmall'}
								isClearable={false}
							/>

							<FormTextInput
								name={`${pensjonPath}.generer.averageG`}
								useOnChange={true}
								onChange={(event: BaseSyntheticEvent) => {
									formMethods.setValue(`${pensjonPath}.generer.averageG`, event?.target?.value)
								}}
								size={'xxsmall'}
								label="Gjenomsnitt G-verdi"
								type="number"
							/>

							<FormCheckbox
								name={`${pensjonPath}.generer.tillatInntektUnder1G`}
								label="Tillat inntekt under 1G"
								size="small"
								wrapperSize="inherit"
								checkboxMargin
							/>

							<Button
								variant={'secondary'}
								onClick={handleGenererChange}
								type="button"
								size="small"
							>
								Generer
							</Button>

							{genererteInntekter?.length > 0 && (
								<StyledPanel>
									<Panel
										heading={getTittel(genererteInntekter)}
										startOpen={true}
										aria-label={'Liste med inntekter'}
									>
										<FormDollyFieldArray
											name={`pensjonforvalter.inntekt.inntekter`}
											header="Inntekt"
											disabled={true}
											canBeEmpty={false}
										>
											{(path: string, idx: number) => (
												<div className="flexbox--flex-wrap sigrun-form" key={idx}>
													<FormTextInput
														name={`${path}.inntekt`}
														label="Inntekt"
														type="number"
														size="small"
														useControlled={true}
													/>
													<FormTextInput
														isDisabled={true}
														name={`${path}.ar`}
														label="År"
														type="number"
														size="xxsmall"
														useControlled={true}
													/>
													<FormTextInput
														isDisabled={true}
														name={`${path}.generatedG`}
														label="Generert G-verdi"
														type="number"
														size="xxsmall"
														useControlled={true}
													/>
													<FormTextInput
														isDisabled={true}
														name={`${path}.grunnbelop`}
														label="Grunnbeløp"
														type="number"
														size="xxsmall"
														useControlled={true}
													/>
												</div>
											)}
										</FormDollyFieldArray>
									</Panel>
								</StyledPanel>
							)}
						</div>
					</Kategori>
				)}
				{inputType === inputValg.fyllInnInntekt && (
					<Kategori title="Pensjonsgivende inntekt" vis={pensjonPath}>
						<div className="flexbox--flex-wrap">
							<FormSelect
								name={`${pensjonPath}.fomAar`}
								label="Fra og med år"
								options={getYearRangeOptions(
									syttenFraOgMedAar || 1968,
									new Date().getFullYear() - 1,
								)}
								size={'xsmall'}
								isClearable={false}
							/>

							<FormSelect
								name={`${pensjonPath}.tomAar`}
								label="Til og med år"
								options={getYearRangeOptions(1968, new Date().getFullYear() - 1)}
								size={'xsmall'}
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
				)}
			</Panel>
		</Vis>
	)
}

PensjonForm.validation = validation
