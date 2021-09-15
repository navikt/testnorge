import * as React from 'react'
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

export interface TelefonnummerValues {
	telefonLandskode_1?: string
	telefonnummer_1?: string
	telefonLandskode_2?: string
	telefonnummer_2?: string
}

interface TelefonnummerProps {
	formikBag: FormikProps<{ tpsf: TelefonnummerValues }>
}

export const Telefonnummer = ({ formikBag }: TelefonnummerProps) => {
	const [harToTlfnr, settToTlfnr, settEttTlfnr] = useBoolean(
		formikBag.values.tpsf.hasOwnProperty('telefonnummer_2')
	)

	const paths = {
		telefonLandskode_1: 'tpsf.telefonLandskode_1',
		telefonnummer_1: 'tpsf.telefonnummer_1',
		telefonLandskode_2: 'tpsf.telefonLandskode_2',
		telefonnummer_2: 'tpsf.telefonnummer_2',
	}

	const leggTilTlfnr = () => {
		formikBag.setFieldValue('tpsf.telefonLandskode_2', '')
		formikBag.setFieldValue('tpsf.telefonnummer_2', '')
		settToTlfnr()
	}

	const fjernTlfnr = () => {
		formikBag.setFieldValue('tpsf.telefonLandskode_2', undefined)
		formikBag.setFieldValue('tpsf.telefonnummer_2', undefined)
		settEttTlfnr()
	}

	return (
		<Vis attributt={Object.values(paths)} formik>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name="tpsf.telefonLandskode_1"
					label="Telefon landkode"
					kodeverk={PersoninformasjonKodeverk.Retningsnumre}
					size="large"
					isClearable={false}
					visHvisAvhuket
				/>
				<FormikTextInput
					name="tpsf.telefonnummer_1"
					label="Telefonnummer"
					type="string"
					size="large"
				/>

				{!harToTlfnr && (
					<Button onClick={leggTilTlfnr} kind="add-circle">
						Telefonnummer
					</Button>
				)}

				{harToTlfnr && (
					<>
						<FormikSelect
							name="tpsf.telefonLandskode_2"
							label="Telefon landkode"
							kodeverk={PersoninformasjonKodeverk.Retningsnumre}
							size="large"
							isClearable={false}
							visHvisAvhuket
						/>
						<FormikTextInput
							name="tpsf.telefonnummer_2"
							label="Telefonnummer"
							type="string"
							size="large"
						/>
						<Button kind="trashcan" onClick={fjernTlfnr} title="Fjern" />
					</>
				)}
			</div>
		</Vis>
	)
}
