import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

const initialValues = {
	identtype: null,
	nyIdent: false,
}

export const Alias = ({ formikBag }) => (
	<FormikDollyFieldArray name="udistub.aliaser" header="Alias" newEntry={initialValues}>
		{(path, idx, curr) => (
			<React.Fragment key={idx}>
				<FormikSelect
					name={`${path}.nyIdent`}
					label="Type alias"
					options={Options('nyIdent')}
					isClearable={false}
				/>
				{curr.nyIdent && (
					<FormikSelect
						name={`${path}.identtype`}
						label="Identtype"
						options={Options('identtypeUtenBost')}
						isClearable={false}
					/>
				)}
			</React.Fragment>
		)}
	</FormikDollyFieldArray>
)
