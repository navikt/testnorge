import React, { Fragment } from 'react'
import { FieldArray } from 'formik'
import { DollyApi } from '~/service/Api'
import Button from '~/components/button/Button'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'

const Attributt = new AttributtManager()

const FormEditorFieldArray = (
	subKategori,
	formikProps,
	renderFieldComponent,
	renderFieldSubItem,
	shouldRenderFieldComponent,
	editMode
) => {
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
						renderFieldSubItem={renderFieldSubItem}
						shouldRenderFieldComponent={shouldRenderFieldComponent}
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
	renderFieldSubItem,
	shouldRenderFieldComponent,
	editMode,
	arrayHelpers
}) => {
	const { subKategori, items, subItems, id } = item

	const parentId = id
	const parentAttributes = items.reduce((prev, curr) => {
		return { ...prev, [curr.id]: Attributt.initValueSelector(curr) }
	}, {})

	const createDefaultObject = () => arrayHelpers.push({ ...parentAttributes })
	const createSubItem = (itemIndex, subItemIndex) => {
		const subItemAttributes = subItems[subItemIndex].items.reduce((prev, curr) => {
			return { ...prev, [curr.id]: Attributt.initValueSelector(curr) }
		}, {})

		let valueCopy = JSON.parse(JSON.stringify(formikProps.values[parentId][0]))

		const subItemId = subItems[subItemIndex].id
		arrayHelpers.replace(itemIndex, { ...valueCopy, [subItemId]: subItemAttributes })
	}

	const formikValues = formikProps.values[parentId]

	return (
		<Fragment>
			<h4>{subKategori.navn}</h4>
			{formikValues && formikValues.length > 0 ? (
				formikValues.map((faKey, idx) => {
					return (
						<div key={idx}>
							{idx !== 0 && <div className="field-array-line" />}
							<div className="subkategori-field-group multi">
								{items.map(item => {
									if (
										shouldRenderFieldComponent(items, item, formikProps, {
											parentId,
											idx
										})
									) {
										// Add subKategori to ID
										const fakeItem = {
											...item,
											id: `${parentId}[${idx}]${item.id}`
										}

										return renderFieldComponent(fakeItem, formikProps.values, {
											parentId,
											idx
										})
									}
								})}
								{/* REG-3377: Alex - Under utvikling */}
								{/* {subItems && subItems.map(subItem => {
									// console.log('subItem :', subItem)

									const fakeSubItem = {
										...subItem,
										id: `${parentId}[${idx}]${subItem.id}`
									}

									return renderFieldSubItem(fakeSubItem)
								})} */}

								{!editMode && (
									<Button
										className="field-group-remove"
										kind="remove-circle"
										onClick={e => arrayHelpers.remove(idx)}
									/>
								)}
							</div>
							{/* REG-3377: Alex - Under utvikling */}

							{/*Attributtene med items og subItems, f.eks AAREGs */}
							{/* {!editMode &&
								subItems &&
								subItems.map((subItem, i) => (
									<Button
										className="flexbox--align-center field-group-add"
										kind="add-circle"
										key={i}
										onClick={() => createSubItem(idx, i)}
									>
										{subItem.label}
									</Button>
								))} */}
						</div>
					)
				})
			) : (
				<p className="ingen-verdi-melding">Ingen verdi lagt til</p>
			)}

			{!editMode && (
				<Button
					className="flexbox--align-center field-group-add"
					kind="add-circle"
					onClick={createDefaultObject}
				>
					{subKategori.navn.toUpperCase()}
				</Button>
			)}
		</Fragment>
	)
}

export default FormEditorFieldArray
