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
import { usePensjonFacadeGjennomsnitt } from '@/utils/hooks/usePensjonFacade'
import { ErrorMessage } from '@hookform/error-message'

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

export const GenererInntektForm = ({ syttenFraOgMedAar, formMethods }) => {
	const { pensjon, mutate } = usePensjonFacadeGjennomsnitt(
		formMethods.watch(`${pensjonGenererPath}.generer`),
	)

	const handleGenerer = () => {
		mutate().then((values) => {
			formMethods.setValue(`${pensjonGenererPath}.inntekter`, values?.data?.arInntektGList)
		})
	}

	useEffect(() => {
		if (pensjon) {
			formMethods.clearErrors(`${pensjonGenererPath}.inntekter`)
			formMethods.setValue(`${pensjonGenererPath}.inntekter`, pensjon.arInntektGList)
		}
	}, [pensjon])

	const genererteInntekter = formMethods.watch(`${pensjonGenererPath}.inntekter`)

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

				{genererteInntekter?.length > 0 && (
					<StyledPanel>
						<Panel
							heading={getTittel(genererteInntekter)}
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
