import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

export const Sivilstand = ({ tpsfPath }) => {
	const initForhold = { sivilstand: '', sivilstandRegdato: '' }

	return (
		<DollyFieldArray name={tpsfPath} title="Forhold" newEntry={initForhold}>
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
		</DollyFieldArray>
	)
}
