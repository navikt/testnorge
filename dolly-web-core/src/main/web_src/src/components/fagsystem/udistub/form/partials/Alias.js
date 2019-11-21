import React, { useState } from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Alias = ({ formikBag }) => {
	const initialValues = formikBag.initialValues.udistub.aliaser[0]

	return (
		<FieldArray
			name="udistub.aliaser"
			render={arrayHelpers => (
				<Kategori title="Alias">
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
				</Kategori>
			)}
		/>
	)
}
