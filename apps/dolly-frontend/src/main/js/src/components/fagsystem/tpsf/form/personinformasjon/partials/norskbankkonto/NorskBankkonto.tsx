import * as React from 'react'
import { useEffect, useState } from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { generateValidKontoOptions } from '~/utils/GenererGyldigNorskBankkonto'
import _get from 'lodash/get'
import { FormikProps } from 'formik'

const initialNorskBankkonto = {
	kontonummer: '',
}

export const NorskBankkonto = ({ formikBag }: { formikBag: FormikProps<{}> }) => {
	const [validKontoOptions, setValidKontoOptions] = useState([])
	const [kontonummer, setKontonummer] = useState(
		_get(formikBag.values, 'tpsMessaging.norskBankkonto[0].kontonummer')
	)

	useEffect(() => {
		setValidKontoOptions(generateValidKontoOptions())
	}, [])
	return (
		<Vis attributt={'tpsMessaging.norskBankkonto'} formik>
			<FormikDollyFieldArray
				name="tpsMessaging.norskBankkonto"
				header="Norsk Bankkonto"
				newEntry={initialNorskBankkonto}
				canBeEmpty={false}
				maxEntries={1}
				maxReachedDescription={'Kun 1 bankkonto støttet foreløpig'}
			>
				{(path: string) => {
					return (
						<div className="flexbox--flex-wrap">
							<FormikSelect
								value={kontonummer}
								placeholder={kontonummer ? kontonummer : 'Velg..'}
								afterChange={(selected: { value: string; label: string }) =>
									setKontonummer(selected.value)
								}
								options={validKontoOptions}
								isClearable={false}
								name={`${path}.kontonummer`}
								label={'Kontonummer'}
							/>
						</div>
					)
				}}
			</FormikDollyFieldArray>
		</Vis>
	)
}
