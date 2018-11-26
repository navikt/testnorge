import React, { Fragment } from 'react'
import { FieldArray } from 'formik'
import { DollyApi } from '~/service/Api'
import Button from '~/components/button/Button'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'

const Attributt = new AttributtManager()

const FormEditorFieldArray = (subKategori, formikProps, renderFieldComponent, editMode) => {
	const parentId = subKategori.id
	return (
		<div className="subkategori" key={parentId}>
			<FieldArray
				name={parentId}
				render={arrayHelpers => (
					<FieldArrayComponent
						item={subKategori}
						formikProps={formikProps}
						renderFieldComponent={renderFieldComponent}
						editMode={editMode}
						arrayHelpers={arrayHelpers}
					/>
				)}
			/>
		</div>
	)
}

export const FieldArrayComponent = ({
	item,
	formikProps,
	renderFieldComponent,
	editMode,
	arrayHelpers
}) => {
	const { subKategori, items, id } = item
	const parentId = id
	const defs = items.reduce((prev, curr) => {
		return { ...prev, [curr.id]: Attributt.initValueSelector(curr) }
	}, {})
	const createDefaultObject = () => arrayHelpers.push({ ...defs })
	return (
		<Fragment>
			<h4>
				{subKategori.navn}
				{!editMode && (
					<Button className="field-group-add" kind="add-circle" onClick={createDefaultObject} />
				)}
			</h4>
			{formikProps.values[parentId] && formikProps.values[parentId].length > 0 ? (
				formikProps.values[parentId].map((faKey, idx) => {
					return (
						<div key={idx}>
							<div className="subkategori-field-group multi">
								{items.map(item => {
									// Add subKategori to ID
									const fakeItem = {
										...item,
										id: `${parentId}[${idx}]${item.id}`
									}
									return renderFieldComponent(fakeItem, formikProps.values, {
										parentId,
										idx
									})
								})}
								{!editMode && (
									<Button
										className="field-group-remove"
										kind="remove-circle"
										onClick={e => arrayHelpers.remove(idx)}
									/>
								)}
							</div>
						</div>
					)
				})
			) : (
				<span>Ingen verdi lagt til</span>
			)}
		</Fragment>
	)
}

export default FormEditorFieldArray
