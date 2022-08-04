import React from 'react'
import { Field, FieldArray, FormikProps } from 'formik'
import Button from '~/components/ui/button/Button'
import { ErrorMessageWithFocus } from '~/utils/ErrorMessageWithFocus'

interface IdentSearchProps {
	formikBag: FormikProps<{}>
}

const identerPath = 'identer'

export const Identer: React.FC<IdentSearchProps> = ({ formikBag }: IdentSearchProps) => {
	// @ts-ignore
	const values = formikBag.values[identerPath]
	return (
		<section>
			<FieldArray name={identerPath}>
				{({ insert, remove, push }) => (
					<div style={{ marginTop: '10px' }}>
						{values?.length > 0 &&
							values.map((ident: string, index: number) => (
								<div className="flexbox--align-start" key={index}>
									<div className="skjemaelement">
										<Field
											name={`${identerPath}.${index}`}
											className="skjemaelement__input"
											placeholder={'Ikke spesifisert'}
											type="text"
											style={{ width: '220px' }}
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
											style={{ margin: '5px 0 0 10px' }}
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
