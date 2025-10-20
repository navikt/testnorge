import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import React, { useEffect } from 'react'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import Panel from '@/components/ui/panel/Panel'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { pensjonGenererPath } from '@/components/fagsystem/pensjon/form/Form'
import styled from 'styled-components'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { usePensjonFacadeGenerer } from '@/utils/hooks/usePensjon'
import { DisplayFormError } from '@/components/ui/toast/DisplayFormError'

const getTittel = (data) => {
	const inntektsaar = data?.map((inntekt) => inntekt.ar)
	const foerste = Math.min(...inntektsaar)
	const siste = Math.max(...inntektsaar)
	return `Genererte inntekter (${foerste} - ${siste})`
}

const StyledButton = styled(NavButton)`
	margin: 0 10px 15px 2px;
`

const StyledPanel = styled.div`
	width: 790px;
`

export const GenerertInntektForm = ({ gyldigFraOgMedAar, formMethods }) => {
	const formInntekter = formMethods.watch(`${pensjonGenererPath}.inntekter`)
	const genererTOM = formMethods.watch(`${pensjonGenererPath}.generer.tomAar`)
	const genererFOM = formMethods.watch(`${pensjonGenererPath}.generer.fomAar`)
	const { pensjonResponse, trigger } = usePensjonFacadeGenerer(
		formMethods.watch(`${pensjonGenererPath}.generer`),
	)

	const handleGenerer = () => {
		formMethods.clearErrors(`manual.${pensjonGenererPath}`)
		formMethods.clearErrors(`${pensjonGenererPath}`)
		trigger()
			.then((values) => {
				if (!values) {
					formMethods.setError(`manual.${pensjonGenererPath}.generer.tomAar`, {
						message: 'Velg et gyldig år',
					})
				}
				formMethods.setValue(`${pensjonGenererPath}.inntekter`, values?.data?.arInntektGList)
			})
			.catch(() => {
				formMethods.setError(`manual.${pensjonGenererPath}.generer.tomAar`, {
					message: 'Velg et gyldig år',
				})
			})
		formMethods.trigger(`${pensjonGenererPath}.generer.tomAar`)
	}

	useEffect(() => {
		trigger()
	}, [])

	useEffect(() => {
		if ((!formInntekter || formInntekter?.length === 0) && pensjonResponse) {
			formMethods.setValue(`${pensjonGenererPath}.inntekter`, pensjonResponse.arInntektGList)
		}
	}, [pensjonResponse])

	return (
		<Kategori
			hjelpetekst={
				'Generer inntekt for hvert år i perioden. Inntekten vil bli generert basert på G-verdi ' +
				'og hver inntekt kan deretter endres manuelt.'
			}
			title="Generert skjema for inntekt"
			vis={pensjonGenererPath}
		>
			<div className="flexbox--flex-wrap">
				<FormSelect
					name={`${pensjonGenererPath}.generer.fomAar`}
					label="Fra og med år"
					options={getYearRangeOptions(
						gyldigFraOgMedAar || 1968,
						(genererTOM && genererTOM - 1) || new Date().getFullYear() - 1,
					)}
					size={'xsmall'}
					isClearable={false}
				/>

				<FormSelect
					name={`${pensjonGenererPath}.generer.tomAar`}
					label="Til og med år"
					options={getYearRangeOptions(
						(genererFOM && genererFOM + 1) || 1968,
						new Date().getFullYear() - 1,
					)}
					size={'xsmall'}
					isClearable={false}
				/>

				<FormTextInput
					name={`${pensjonGenererPath}.generer.averageG`}
					size={'xxsmall'}
					label="Gjenomsnitt G-verdi"
					type="number"
				/>

				<FormCheckbox
					name={`${pensjonGenererPath}.generer.tillatInntektUnder1G`}
					label="Tillat inntekt under 1G"
					size="small"
					wrapperSize="inherit"
					checkboxMargin
				/>

				<div className={'flexbox--full-width'}>
					<StyledButton variant={'primary'} onClick={handleGenerer} type="button" size="small">
						Generer
					</StyledButton>
				</div>

				<DisplayFormError path={`${pensjonGenererPath}.inntekter`} />

				{formInntekter?.length > 0 && (
					<StyledPanel>
						<Panel
							heading={getTittel(formInntekter)}
							startOpen={true}
							aria-label={'Liste med inntekter'}
						>
							<FormDollyFieldArray
								name={`${pensjonGenererPath}.inntekter`}
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
											onBlur={() => {
												formMethods.setValue(`${path}.generatedG`, 0)
												formMethods.setValue(`${path}.grunnbelop`, 0)
											}}
										/>
										<FormTextInput
											isDisabled={true}
											name={`${path}.ar`}
											label="År"
											type="number"
											size="xxsmall"
										/>
										<FormTextInput
											isDisabled={true}
											name={`${path}.generatedG`}
											label="Generert G-verdi"
											type="number"
											size="xxsmall"
										/>
										<FormTextInput
											isDisabled={true}
											name={`${path}.grunnbelop`}
											label="Grunnbeløp"
											type="number"
											size="xxsmall"
										/>
									</div>
								)}
							</FormDollyFieldArray>
						</Panel>
					</StyledPanel>
				)}
			</div>
		</Kategori>
	)
}
