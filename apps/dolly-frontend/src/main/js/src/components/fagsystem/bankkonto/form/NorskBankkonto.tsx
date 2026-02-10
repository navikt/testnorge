import * as React from 'react'
import { useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { generateValidKontoOptions } from '@/utils/GenererGyldigNorskBankkonto'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { UseFormReturn } from 'react-hook-form/dist/types'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'

export const NorskBankkonto = ({ formMethods }: { formMethods: UseFormReturn }) => {
	const [validKontoOptions, setValidKontoOptions] = useState([])
	const path = 'bankkonto.norskBankkonto'
	const harTilfeldig = formMethods.watch(`${path}.tilfeldigKontonummer`)
	const kontonummer = formMethods.watch(`${path}.kontonummer`)

	useEffect(() => {
		setValidKontoOptions(generateValidKontoOptions(kontonummer))
	}, [])

	return (
		<Vis attributt={path}>
			<Panel
				heading={'Norsk bankkonto'}
				iconType="bankkonto"
				hasErrors={panelError(path)}
				startOpen={erForsteEllerTest(formMethods.getValues(), [path])}
			>
				<FormSelect
					placeholder={'Velg ...'}
					options={validKontoOptions}
					isClearable={true}
					name={`${path}.kontonummer`}
					label={'Kontonummer'}
					isDisabled={harTilfeldig}
				/>
				<div style={{ marginTop: '25px' }}>
					<FormCheckbox
						name={`${path}.tilfeldigKontonummer`}
						label="Har tilfeldig kontonummer"
						afterChange={() => {
							formMethods.trigger(`${path}.kontonummer`)
						}}
						disabled={kontonummer}
					/>
				</div>
			</Panel>
		</Vis>
	)
}
