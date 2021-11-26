import * as React from 'react'
import { useEffect, useState } from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { generateValidKontoOptions } from '~/utils/GenererGyldigNorskBankkonto'

const initialNorskBankkonto = {
	kontonummer: '',
}

export const NorskBankkonto = () => {
	const [validKontoOptions, setValidKontoOptions] = useState([])

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
			>
				{(path: string) => {
					return (
						<div className="flexbox--flex-wrap">
							<FormikSelect
								options={validKontoOptions}
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
