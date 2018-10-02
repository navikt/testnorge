import React, { Fragment } from 'react'
import { FieldArray } from 'formik'
import { DollyApi } from '~/service/Api'
import Button from '~/components/button/Button'

const FormEditorFieldArray = ({ subKategori, items, id }, formikProps, renderFieldComponent) => {
	const parentId = id

	return (
		<div className="subkategori" key={parentId}>
			<FieldArray
				name={parentId}
				render={arrayHelpers => {
					const defs = items.reduce((prev, curr) => ({ ...prev, [curr.id]: '' }), {})
					const createDefaultObject = () => arrayHelpers.push({ ...defs })
					return (
						<Fragment>
							<h4>
								{subKategori.navn}
								<Button
									className="field-group-add"
									kind="add-circle"
									onClick={createDefaultObject}
								/>
							</h4>
							{formikProps.values[parentId] && formikProps.values[parentId].length > 0 ? (
								formikProps.values[parentId].map((faKey, idx) => {
									return (
										<div key={idx}>
											<div className="subkategori-field-group">
												{items.map(item => {
													// Add subKategori to ID
													const fakeItem = {
														...item,
														id: `${parentId}[${idx}]${item.id}`
													}
													return renderFieldComponent(fakeItem)
												})}
												<Button
													className="field-group-remove"
													kind="remove-circle"
													onClick={e => arrayHelpers.remove(idx)}
												/>
											</div>
										</div>
									)
								})
							) : (
								<span>Ingen barn lagt til</span>
							)}
						</Fragment>
					)
				}}
			/>
		</div>
	)
}

export default FormEditorFieldArray
