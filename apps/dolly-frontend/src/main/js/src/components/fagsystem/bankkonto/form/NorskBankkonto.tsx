import * as React from 'react'
import { useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { generateValidKontoOptions } from '@/utils/GenererGyldigNorskBankkonto'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

export const NorskBankkonto = ({ formikBag }: { formikBag: FormikProps<{}> }) => {
	const [validKontoOptions, setValidKontoOptions] = useState([])

	useEffect(() => {
		setValidKontoOptions(generateValidKontoOptions())
	}, [])

	const path = 'bankkonto.norskBankkonto'
	const harTilfeldig = _get(formikBag?.values, `${path}.tilfeldigKontonummer`)
	const kontonummer = _get(formikBag?.values, `${path}.kontonummer`)

	return (
		<Vis attributt={path} formik>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					placeholder={kontonummer ? kontonummer : 'Velg..'}
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
						size="x-small"
						isDisabled={kontonummer}
					/>
				</div>
			</div>
		</Vis>
	)
}
