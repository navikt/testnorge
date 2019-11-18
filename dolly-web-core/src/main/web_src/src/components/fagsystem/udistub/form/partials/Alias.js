import React, { useState } from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Alias = ({ formikBag }) => {
	const initialValuesAlias = {
		nyIdent: '',
		identtype: ''
	}
	const aliasArray = _get(formikBag.values, 'udistub.aliaser', [])

	const fjern = (idx, path, currentArray) => {
		const nyArray = currentArray.filter((_, _idx) => _idx !== idx)
		formikBag.setFieldValue(path, nyArray)
	}

	return (
		<Kategori title="Alias">
			<FieldArray name="udistub.aliaser">
				{({ push }) => (
					<div>
						{aliasArray.map((alias, idx) => (
							<React.Fragment key={idx}>
								<div className="flexbox">
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
										/>
									)}
									<FieldArrayRemoveButton
										onClick={() => fjern(idx, 'udistub.aliaser', aliasArray)}
									/>
								</div>
							</React.Fragment>
						))}
						<FieldArrayAddButton title="Legg til alias" onClick={() => push(initialValuesAlias)} />
					</div>
				)}
			</FieldArray>
		</Kategori>
	)
}
