import React, { Fragment } from 'react'
import { FieldArray } from 'formik'
import { DollyApi } from '~/service/Api'
import Button from '~/components/button/Button'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'
import cn from 'classnames'

const Attributt = new AttributtManager()

const FormEditorFieldArray = (
	subKategori,
	formikProps,
	renderFieldComponent,
	renderFieldSubItem,
	shouldRenderFieldComponent,
	editMode,
	shouldRenderSubItem
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
						shouldRenderSubItem={shouldRenderSubItem}
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
	shouldRenderSubItem,
	editMode,
	arrayHelpers
}) => {
	const { subKategori, items, subItems, id } = item

	const parentId = id
	const parentAttributes = items.reduce((prev, curr) => {
		return {
			...prev,
			[curr.id]: Boolean(curr.subItems)
				? [Attributt.initValueSelector(curr)]
				: Attributt.initValueSelector(curr)
		}
	}, {})
	const createDefaultObject = () => arrayHelpers.push({ ...parentAttributes })

	const createSubItem = (subitem, itemIndex) => {
		let subItemArray = subitem.subItems
		const subItemId = subitem.id

		const subItemAttributes = subItemArray.reduce((prev, curr) => {
			return { ...prev, [curr.id]: Attributt.initValueSelector(curr) }
		}, {})

		let valueCopy = JSON.parse(JSON.stringify(formikProps.values[parentId][itemIndex]))
		let subArray = valueCopy[subItemId]

		if (subArray && subArray.length > 0 && subArray[0] != '') {
			subArray.push(subItemAttributes)
		} else {
			subArray = [subItemAttributes]
		}
		arrayHelpers.replace(itemIndex, { ...valueCopy, [subItemId]: subArray })
	}

	const removeSubItem = (itemIndex, subItemIndex, subItem) => {
		const valueCopy = Object.assign(formikProps.values[parentId][itemIndex])
		let subItemArr = valueCopy[subItem]
		subItemArr && subItemArr.splice(subItemIndex, 1)

		arrayHelpers.replace(itemIndex, { ...valueCopy, [subItem]: subItemArr })
	}
	const formikValues = formikProps.values[parentId]
	let subLabelArray = []
	return (
		<Fragment>
			{formikValues && formikValues.length > 0 ? (
				formikValues.map((faKey, idx) => {
					return (
						<div key={idx}>
							{idx !== 0 && <div className="field-array-line" />}
							<div style={{ display: 'flex' }}>
								<div className="subkategori-field-group multi">
									{items.map((item, kdx) => {
										if (
											item.subKategori.id !== subKategori.navn &&
											!subLabelArray.includes(item.subKategori.id)
										) {
											subLabelArray.push(item.subKategori.id)
											var visUnderoverskrift = true
										}
										if (item.subItems) {
											return (
												<div key={kdx}>
													{visUnderoverskrift && <h4>{item.subKategori.navn}</h4>}
													{faKey[item.id] &&
														faKey[item.id].map((subRad, jdx) => {
															if (shouldRenderSubItem(item, formikProps, idx)) {
																return (
																	<div key={jdx} className="subItems">
																		<div className="subItem-header">
																			<div style={{ display: 'flex' }}>
																				{jdx === 0 && <h4>{item.label}</h4>}
																				{item.informasjonstekst && (
																					<ContentTooltip>
																						<span>{item.informasjonstekst}</span>
																					</ContentTooltip>
																				)}
																			</div>
																		</div>
																		<div className="subitem-container-button">
																			{renderFieldSubItem(
																				formikProps,
																				item,
																				subRad,
																				parentId,
																				idx,
																				jdx
																			)}
																			{!editMode && (
																				//item.isMultiple &&
																				<Button
																					className="field-group-remove"
																					kind="remove-circle"
																					onClick={() => removeSubItem(idx, jdx, item.id)}
																					title="Fjern"
																					children={item.label.toUpperCase()}
																				/>
																			)}
																		</div>
																	</div>
																)
															}
														})}
												</div>
											)
											//}
										} else if (
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
											return (
												<div key={kdx} className="flexbox">
													{renderFieldComponent(
														fakeItem,
														formikProps.values,
														{
															parentId,
															idx
														},
														formikProps
													)}
												</div>
											)
										}
									})}
								</div>
								<div>
									{!editMode &&
										item.isMultiple && (
											<Button
												className="field-group-remove"
												kind="remove-circle"
												onClick={e => arrayHelpers.remove(idx)}
												title="Fjern"
												children={subKategori.navn.toUpperCase()}
											/>
										)}
								</div>
							</div>
							{items.map((item, ndx) => {
								return (
									item.subItems && (
										// item.isMultiple &&
										<Button
											className="flexbox--align-center field-group-add"
											kind="add-circle"
											onClick={() => createSubItem(item, idx)}
											key={ndx}
										>
											{item.label.toUpperCase()}
										</Button>
									)
								)
							})}
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

			{!editMode &&
				item.isMultiple && (
					<Button
						className="flexbox--align-center field-group-add"
						kind="add-circle"
						onClick={createDefaultObject}
						// key={idx}
					>
						{subKategori.navn.toUpperCase()}
					</Button>
				)}
		</Fragment>
	)
}

export default FormEditorFieldArray
