import * as React from 'react'
import { useEffect, useState } from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { generateValidKontoOptions } from '~/utils/GenererGyldigNorskBankkonto'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import styled from 'styled-components'

const Title = styled.h4`
	width: 100%;
`

export const NorskBankkonto = ({ formikBag }: { formikBag: FormikProps<{}> }) => {
	const [validKontoOptions, setValidKontoOptions] = useState([])
	const [kontonummer, setKontonummer] = useState(
		_get(formikBag.values, 'tpsMessaging.norskBankkonto.kontonummer')
	)

	const path = 'tpsMessaging.norskBankkonto'

	useEffect(() => {
		setValidKontoOptions(generateValidKontoOptions())
	}, [])
	return (
		<Vis attributt={path} formik>
			<div className="flexbox--flex-wrap">
				<Title>Norsk bankkonto</Title>
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
		</Vis>
	)
}
