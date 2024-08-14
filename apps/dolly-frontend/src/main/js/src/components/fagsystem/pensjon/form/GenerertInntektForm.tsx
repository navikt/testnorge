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
import { ErrorMessage } from '@hookform/error-message'
import { usePensjonFacadeGenerer } from '@/utils/hooks/usePensjon'

const getTittel = (data) => {
	const inntektsaar = data?.map((inntekt) => inntekt.ar)
	const foerste = Math.min(...inntektsaar)
	const siste = Math.max(...inntektsaar)
	return `Genererte inntekter (${foerste} - ${siste})`
}

const StyledButton = styled(NavButton)`
	margin: 10px 10px 10px 2px;
`

const StyledPanel = styled.div`
	width: 790px;
`

export const GenerertInntektForm = ({ syttenFraOgMedAar, formMethods }) => {
	const formInntekter = formMethods.watch(`${pensjonGenererPath}.inntekter`)
	const { pensjonResponse, mutate } = usePensjonFacadeGenerer(
		formMethods.watch(`${pensjonGenererPath}.generer`),
	)

	const handleGenerer = () => {
		formMethods.clearErrors(`${pensjonGenererPath}.inntekter`)
		mutate().then((values) => {
			formMethods.setValue(`${pensjonGenererPath}.inntekter`, values?.data?.arInntektGList)
		})
	}

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
			title="Generert skjema inntekt"
			vis={pensjonGenererPath}
		>
			<div className="flexbox--flex-wrap">
				<FormSelect
					name={`${pensjonGenererPath}.generer.fomAar`}
					label="Fra og med år"
					options={getYearRangeOptions(syttenFraOgMedAar || 1968, new Date().getFullYear() - 1)}
					size={'xsmall'}
					isClearable={false}
				/>

				<FormSelect
					name={`${pensjonGenererPath}.generer.tomAar`}
					label="Til og med år"
					options={getYearRangeOptions(1968, new Date().getFullYear() - 1)}
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

				<StyledButton variant={'secondary'} onClick={handleGenerer} type="button" size="small">
					Generer
				</StyledButton>

				{formMethods.formState.errors && (
					<ErrorMessage
						name={`${pensjonGenererPath}.inntekter`}
						errors={formMethods.formState.errors}
						render={({ message }) => (
							<p style={{ color: '#ba3a26', fontStyle: 'italic' }}>{message}</p>
						)}
					/>
				)}

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
	)
}
