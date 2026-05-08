import { useFormContext } from 'react-hook-form'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { kelvinAapPath } from '@/components/fagsystem/kelvin/initialValues'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, usePanelError } from '@/components/ui/form/formUtils'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import * as React from 'react'
import * as Yup from 'yup'
import { ifPresent, requiredBoolean } from '@/utils/YupValidations'
import styled from 'styled-components'
import { useEffect } from 'react'

const CheckboxWrapper = styled.div`
	display: flex;
	flex-wrap: wrap;
	width: 100%;

	&& {
		.dolly-form-input {
			min-width: 0;
			flex-grow: 0;
		}
	}
`

export const KelvinAapForm = () => {
	const formMethods = useFormContext()

	const andreUtbetalingerPath = `${kelvinAapPath}.andreUtbetalinger`

	const harAfp = formMethods
		.getValues(`${kelvinAapPath}.andreUtbetalinger.stoenad`)
		?.includes('AFP')

	useEffect(() => {
		if (!harAfp) {
			formMethods.setValue(`${kelvinAapPath}.andreUtbetalinger.afp.hvemBetaler`, '')
		}
	}, [harAfp])

	return (
		<Vis attributt={kelvinAapPath}>
			<Panel
				heading="Nav AAP ytelse"
				hasErrors={usePanelError(kelvinAapPath)}
				iconType="arena"
				startOpen={erForsteEllerTest(formMethods.getValues(), [kelvinAapPath])}
			>
				<h3 style={{ marginTop: 0 }}>Generelt</h3>
				<CheckboxWrapper>
					<FormCheckbox name={`${kelvinAapPath}.erStudent`} label="Er student" size="small" />
					<FormCheckbox
						name={`${kelvinAapPath}.harMedlemskap`}
						label="Har medlemskap i folketrygden"
						size="small"
					/>
					<FormCheckbox
						name={`${kelvinAapPath}.harYrkesskade`}
						label="Har yrkesskade"
						size="small"
					/>
				</CheckboxWrapper>
				<h3>Andre ytelser/utbetalinger (samordning)</h3>
				<div className={'flexbox--full-width'}>
					<FormSelect
						name={`${andreUtbetalingerPath}.stoenad`}
						label="Stønad"
						options={Options('kelvinAapStoenad')}
						size="grow"
						isMulti
					/>
				</div>
				<div className={'flexbox--flex-wrap'}>
					{harAfp && (
						<FormTextInput
							name={`${andreUtbetalingerPath}.afp.hvemBetaler`}
							label="Hvem betaler (AFP)"
						/>
					)}
					<FormSelect
						name={`${andreUtbetalingerPath}.loenn`}
						label="Lønn"
						options={Options('jaNei')}
						size="xsmall"
					/>
				</div>
			</Panel>
		</Vis>
	)
}

KelvinAapForm.validation = {
	kelvinAap: ifPresent(
		'$kelvinAap',
		Yup.object({
			andreUtbetalinger: Yup.object({
				afp: Yup.object({
					hvemBetaler: Yup.string().nullable(),
				}),
				loenn: Yup.string().nullable(),
				stoenad: Yup.array().of(Yup.string()).min(1, 'Velg minst én stønad'),
			}),
			erStudent: requiredBoolean,
			harMedlemskap: requiredBoolean,
			harYrkesskade: requiredBoolean,
		}),
	),
}
