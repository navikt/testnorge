import React, { BaseSyntheticEvent } from 'react'
import { Field, FieldArray, FormikProps } from 'formik'
import Button from '@/components/ui/button/Button'
import { ErrorMessageWithFocus } from '@/utils/ErrorMessageWithFocus'
import { CypressSelector } from '../../../../../cypress/mocks/Selectors'

interface IdentSearchProps {
	formikBag: FormikProps<{}>
}

const identerPath = 'identer'

export const Identer: React.FC<IdentSearchProps> = ({ formMethods }: IdentSearchProps) => {
	// @ts-ignore
	const values = formikBag.values[identerPath]
	return (
		<section>
			<FieldArray name={identerPath}>
				{({ insert, remove, push }) => (
					<div style={{ marginTop: '10px' }}>
						{values?.length > 0 &&
							values.map((_ident: string, index: number) => (
								<div className="flexbox--align-start" key={index}>
									<div className="skjemaelement">
										<Field
											data-cy={CypressSelector.INPUT_TESTNORGE_FNR}
											name={`${identerPath}.${index}`}
											className="skjemaelement__input"
											placeholder={'Ikke spesifisert'}
											label={'Fødselsnummer eller D-dummer'}
											type="text"
											style={{ width: '220px' }}
											onKeyPress={(event: BaseSyntheticEvent<KeyboardEvent>) => {
												event.nativeEvent.code === 'Enter' &&
													!Object.keys(formikBag.errors).length &&
													formikBag.handleSubmit()
											}}
										/>
										<ErrorMessageWithFocus
											name={`${identerPath}.${index}`}
											className="skjemaelement__feilmelding"
											component="div"
										/>
									</div>
									{values.length > 1 && (
										<Button
											onClick={() => remove(index)}
											kind="trashcan"
											fontSize={'1.5rem'}
											style={{ marginLeft: '10px' }}
										/>
									)}
								</div>
							))}
						<Button onClick={() => push('')} kind="add-circle" style={{ margin: '0 0 10px 5px' }}>
							Legg til flere
						</Button>
					</div>
				)}
			</FieldArray>
		</section>
	)
}

export const IdenterPaths = [identerPath]
