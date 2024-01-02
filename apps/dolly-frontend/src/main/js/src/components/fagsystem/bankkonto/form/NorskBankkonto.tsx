import * as React from 'react'
import { useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { generateValidKontoOptions } from '@/utils/GenererGyldigNorskBankkonto'
import * as _ from 'lodash'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { UseFormReturn } from 'react-hook-form/dist/types'

export const NorskBankkonto = ({ formMethods }: { formMethods: UseFormReturn }) => {
	const [validKontoOptions, setValidKontoOptions] = useState([])
	const path = 'bankkonto.norskBankkonto'
	const harTilfeldig = _.get(formMethods.getValues(), `${path}.tilfeldigKontonummer`)
	const kontonummer = _.get(formMethods.getValues(), `${path}.kontonummer`)

	useEffect(() => {
		setValidKontoOptions(generateValidKontoOptions(kontonummer))
	}, [])

	return (
		<Vis attributt={path} formik>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					placeholder={'Velg ...'}
					options={validKontoOptions}
					isClearable={true}
					name={`${path}.kontonummer`}
					label={'Kontonummer'}
					isDisabled={harTilfeldig}
				/>
				<div style={{ marginTop: '17px' }}>
					<FormikCheckbox
						name={`${path}.tilfeldigKontonummer`}
						label="Har tilfeldig kontonummer"
						size="medium"
						isDisabled={kontonummer}
					/>
				</div>
			</div>
		</Vis>
	)
}
