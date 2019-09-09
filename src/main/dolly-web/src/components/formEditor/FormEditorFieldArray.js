import React, { Fragment } from 'react'
import GenererSyntVerdier from '~/components/genererSyntVerdier/GenererSyntVerdier'
import { Field, FieldArray } from 'formik'
import { DollyApi } from '~/service/Api'
import Button from '~/components/button/Button'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'
import cn from 'classnames'
import InputSelector from '~/components/fields/InputSelector'

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
	arrayHelpers,
	idx
}) => {
	const { subKategori, items, subItems, id } = item
	const parentId = id
	const itemid = idx

	let parentAttributes = items.reduce((prev, curr) => {
		return {
			...prev,
			[curr.id]: Boolean(curr.subItems)
				? [Attributt.initValueSelector(curr)]
				: Attributt.initValueSelector(curr)
		}
	}, {})
	const createDefaultObject = () => {
		// Refaktoreres. Hvordan kan vi generalisere denne typen attributter for barn?
		if ('barn_utvandret' in parentAttributes) {
			parentAttributes.barn_utvandret = [{ utvandretTilLand: '', utvandretTilLandFlyttedato: '' }]
		} else if ('barn_innvandret' in parentAttributes) {
			parentAttributes.barn_innvandret = [
				{ innvandretFraLand: '', innvandretFraLandFlyttedato: '' }
			]
		}
		if ('barn_forsvunnet' in parentAttributes) {
			parentAttributes.barn_forsvunnet = [{ erForsvunnet: '', forsvunnetDato: '' }]
		}
		arrayHelpers.push({ ...parentAttributes })
	}

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
	let formikValues = formikProps.values[parentId]

	//Refaktorerers. Hvordan kan vi generalisere denne typen attributter for barn?
	if (item.id === 'barn_utvandret') {
		formikValues = [{ utvandretTilLand: '', utvandretTilLandFlyttedato: '' }]
	} else if (item.id === 'barn_innvandret') {
		formikValues = [{ innvandretFraLand: '', innvandretFraLandFlyttedato: '' }]
	}
	if (item.id === 'barn_forsvunnet') {
		formikValues = [{ erForsvunnet: '', forsvunnetDato: '' }]
		// formikValues = formikProps.values.barn[idx][parentId]
	}
	let subLabelArray = []
	let antallInstanser = 0

	return (
		<Fragment key={item.id}>
			{formikValues && formikValues.length > 0 ? (
				formikValues.map((faKey, idx) => {
					antallInstanser = idx + 1
					return (
						<Fragment key={idx}>
							{idx !== 0 && <div className="field-array-line" />}
							<div className="flexbox">
								<div className="subkategori-field-group multi">
									{items.map((item, kdx) => {
										if (item.items) {
											return FieldArrayComponent({
												item,
												formikProps,
												renderFieldComponent,
												renderFieldSubItem,
												shouldRenderFieldComponent,
												shouldRenderSubItem,
												editMode,
												arrayHelpers,
												idx
											})
										}
										if (
											item.subKategori.id !== subKategori.navn &&
											!subLabelArray.includes(item.subKategori.id)
										) {
											subLabelArray.push(item.subKategori.id)
											var visUnderoverskrift = true
										}
										if (item.subItems && shouldRenderSubItem(item, formikProps, idx))
											// Render array i array. F.eks. permisjon under arbeidsforhold
											return (
												<div key={kdx}>
													{visUnderoverskrift && <h4>{item.subKategori.navn}</h4>}
													{faKey[item.id] &&
														faKey[item.id].map((subRad, jdx) => {
															return renderHeaderSubFieldButton(
																renderFieldSubItem,
																removeSubItem,
																formikProps,
																item,
																subRad,
																parentId,
																editMode,
																idx,
																jdx
															)
														})}
												</div>
											)
										else if (
											shouldRenderFieldComponent(items, item, formikProps, {
												parentId,
												idx
											})
										) {
											// Add subKategori to ID
											const fakeItem = {
												...item,
												id:
													parentId.includes('barn_innvandret') ||
													parentId.includes('barn_utvandret')
														? //Refaktorerers. Hvordan kan vi generalisere denne typen attributter for barn?
														  `barn[${itemid}]${parentId}[0]${item.id}`
														: `${parentId}[${idx}]${item.id}`
											}
											if (fakeItem.id === 'barn_forsvunnet[0]erForsvunnet') {
												fakeItem.id = `barn[${itemid}]barn_forsvunnet[0]erForsvunnet`
											}
											if (fakeItem.id === 'barn_forsvunnet[0]forsvunnetDato') {
												fakeItem.id = `barn[${itemid}]barn_forsvunnet[0]forsvunnetDato`
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
									item.subItems &&
									addButton(() => createSubItem(item, idx), item.label.toUpperCase(), ndx)
								)
							})}
						</Fragment>
					)
				})
			) : (
				<p className="ingen-verdi-melding">Ingen verdi lagt til</p>
			)}
			{
				<div className="flexbox">
					<div>
						{!editMode &&
							item.isMultiple &&
							addButton(createDefaultObject, subKategori.navn.toUpperCase())}
					</div>
					{/* <div>
						{item.genererSyntVerdier && (
							<GenererSyntVerdier
								type={item.id}
								formikValues={formikValues}
								antall={antallInstanser}
							/>
						)}
					</div> */}
				</div>
			}
		</Fragment>
	)
}

export const addButton = (onClick, header, key) => {
	return (
		<Button
			className="flexbox--align-center field-group-add"
			kind="add-circle"
			onClick={onClick}
			key={key}
		>
			{header}
		</Button>
	)
}
export const renderHeaderSubFieldButton = (
	renderFieldSubItem,
	removeSubItem,
	formikProps,
	item,
	subRad,
	parentId,
	editMode,
	idx,
	jdx
) => {
	return (
		<div key={jdx} className="subItems">
			{jdx === 0 && (
				<div className="flexbox">
					<h4>{item.label}</h4>
					{item.informasjonstekst && (
						//Fjernes når (/hvis) vi får inn validering av datoer på tvers av items
						<h5 className="infotekst">{item.informasjonstekst}</h5>
					)}
				</div>
			)}
			<div className="subitem-container-button">
				{renderFieldSubItem(formikProps, item, subRad, parentId, idx, jdx)}
				{!editMode && (
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
export default FormEditorFieldArray
