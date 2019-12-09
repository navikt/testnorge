import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

const initialValues = {
	identtype: '',
	nyIdent: false
}

export const Alias = ({ formikBag }) => (
	<Kategori title="Alias" vis="udistub.aliaser">
		<FieldArray name="udistub.aliaser">
			{arrayHelpers => (
				<React.Fragment>
					{formikBag.values.udistub.aliaser.map((alias, idx) => (
						<div key={idx}>
							<FormikSelect
								name={`udistub.aliaser[${idx}].nyIdent`}
								label="Type alias"
								options={Options('nyIdent')}
							/>
							{alias.nyIdent && (
								<FormikSelect
									name={`udistub.aliaser[${idx}].identtype`}
									label="Identtype"
									options={Options('identtypeUtenBost')}
									isClearable={false}
								/>
							)}
							<FieldArrayRemoveButton onClick={e => arrayHelpers.remove(idx)} />
						</div>
					))}

					<FieldArrayAddButton
						title="Legg til alias"
						onClick={e => arrayHelpers.push(initialValues)}
					/>
				</React.Fragment>
			)}
		</FieldArray>
	</Kategori>
)
