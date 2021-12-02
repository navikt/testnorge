import React, { useEffect } from 'react'
// @ts-ignore
import useBoolean from '~/utils/hooks/useBoolean'
// @ts-ignore
import Button from '~/components/ui/button/Button'
// @ts-ignore
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
// @ts-ignore
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
// @ts-ignore
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

import { FormikProps } from 'formik'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'

export interface TelefonnummerArray {
	person: {
		telefonnummer: Array<TelefonnummerValues>
	}
}

interface TelefonnummerValues {
	landskode?: string
	nummer?: string
}

interface TelefonnummerProps {
	formikBag: FormikProps<{ pdldata: TelefonnummerArray }>
}

const initialTelefonnummer = {
	landskode: '',
	nummer: '',
	prioritet: 2,
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

export const Telefonnummer = ({ formikBag }: TelefonnummerProps) => {
	const tlfListe = _get(formikBag.values, 'pdldata.person.telefonnummer')
	if (!tlfListe) return null

	useEffect(() => {
		if (tlfListe.length === 1) {
			formikBag.setFieldValue('pdldata.person.telefonnummer[0].prioritet', 1)
		}
	}, [tlfListe])

	const optionsPrioritet = () => {
		if (tlfListe.length === 1) {
			return [{ value: 1, label: '1' }]
		} else
			return [
				{ value: 1, label: '1' },
				{ value: 2, label: '2' },
			]
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.telefonnummer'}
				header="Telefonnummer"
				newEntry={initialTelefonnummer}
				disabled={tlfListe.length > 1}
				title={tlfListe.length > 1 ? 'En person kan maksimalt ha to telefonnumre' : null}
				canBeEmpty={false}
			>
				{(path: string) => (
					<>
						<FormikSelect
							name={`${path}.landskode`}
							label="Landkode"
							kodeverk={PersoninformasjonKodeverk.Retningsnumre}
							size="large"
							isClearable={false}
						/>
						<FormikTextInput
							name={`${path}.nummer`}
							label="Telefonnummer"
							type="string"
							size="large"
						/>
						<FormikSelect
							name={`${path}.prioritet`}
							label="Prioritet"
							options={optionsPrioritet()}
							size="xsmall"
							isClearable={false}
						/>
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
