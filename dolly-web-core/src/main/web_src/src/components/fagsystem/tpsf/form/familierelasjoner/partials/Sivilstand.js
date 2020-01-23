import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

export const Sivilstand = ({ basePath }) => {
	const initForhold = { sivilstand: '', sivilstandRegdato: '' }

	return (
		<FormikDollyFieldArray name={basePath} title="Forhold" newEntry={initForhold} nested>
			{(path, idx) => (
				<React.Fragment key={idx}>
					<FormikSelect
						name={`${path}.sivilstand`}
						label="Forhold til partner (sivilstand)"
						kodeverk="Sivilstander"
						isClearable={false}
					/>
					<FormikDatepicker
						name={`${path}.sivilstandRegdato`}
						label="Sivilstand fra dato"
						isClearable={false}
					/>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
