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

export const KelvinAapForm = () => {
	const formMethods = useFormContext()

	const andreUtbetalingerPath = `${kelvinAapPath}.andreUtbetalinger`

	return (
		<Vis attributt={kelvinAapPath}>
			<Panel
				heading="Kelvin AAP"
				hasErrors={usePanelError(kelvinAapPath)}
				iconType="arena"
				startOpen={erForsteEllerTest(formMethods.getValues(), [kelvinAapPath])}
			>
				<div className={'flexbox--flex-wrap'}>
					{/*TODO: Stoenad er paakrevd*/}
					<h3 style={{ marginTop: 0 }}>Opprett og fullfør behandling</h3>
					<div className={'flexbox--full-width'}>
						<FormSelect
							name={`${andreUtbetalingerPath}.stoenad`}
							label="Stønad"
							options={Options('kelvinAapStoenad')}
							size="grow"
							isMulti
						/>
					</div>
					<FormTextInput name={`${andreUtbetalingerPath}.afp.hvemBetaler`} label="Hvem betaler" />
					<FormSelect
						name={`${andreUtbetalingerPath}.loenn`}
						label="Lønn"
						options={Options('jaNei')}
						size="xsmall"
					/>
					<div className={'flexbox--flex-wrap'}>
						<FormCheckbox
							name={`${kelvinAapPath}.erStudent`}
							label="Er student"
							size="small"
							checkboxMargin
						/>
						<FormCheckbox
							name={`${kelvinAapPath}.harMedlemskap`}
							label="Har medlemskap"
							size="small"
							checkboxMargin
						/>
						<FormCheckbox
							name={`${kelvinAapPath}.harYrkesskade`}
							label="Har yrkesskade"
							size="small"
							checkboxMargin
						/>
					</div>
				</div>
			</Panel>
		</Vis>
	)
}
