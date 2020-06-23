import React from 'react'
import * as Yup from 'yup'
import { requiredString, ifPresent } from '~/utils/YupValidations'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { FormikProps } from 'formik'

interface DokarkivForm {
	formikBag: FormikProps<{}>
}

enum Kodeverk {
	TEMA = 'Tema',
	NAVSKJEMA = 'NAVSkjema'
}

const dokarkivAttributt = 'dokarkiv'

export const DokarkivForm = ({ formikBag }: DokarkivForm) => {
	const handleTittelChange = (v: React.ChangeEvent<any>) => {
		formikBag.setFieldValue('dokarkiv.dokumenter.tittel', v.target.value)
	}

	return (
		// @ts-ignore
		<Vis attributt={dokarkivAttributt}>
			<Panel
				heading="Dokumenter"
				hasErrors={panelError(formikBag, dokarkivAttributt)}
				iconType="dokarkiv"
				startOpen={() => erForste(formikBag.values, [dokarkivAttributt])}
			>
				<Kategori title="Oppretting av skannet dokument" vis={dokarkivAttributt}>
					<FormikTextInput
						name="dokarkiv.tittel"
						label="Tittel"
						onBlur={handleTittelChange}
						size="large"
					/>
					<FormikSelect name="dokarkiv.tema" label="Tema" kodeverk={Kodeverk.TEMA} size="xlarge" />
					<FormikTextInput name="dokarkiv.journalfoerendeEnhet" label="Journalførende enhet" />
				</Kategori>
				<Kategori title="Dokument" vis={dokarkivAttributt}>
					<FormikSelect
						name="dokarkiv.dokumenter.brevkode"
						label="Brevkode"
						kodeverk={Kodeverk.NAVSKJEMA} // Er tom
						size="xxlarge"
					/>
					{/* <FormikTextInput name="dokarkiv.dokumenter.brevkode" label="Brevkode" size="xxlarge" /> */}
					{/* Kanskje bare mest brukte som Options */}
				</Kategori>
			</Panel>
		</Vis>
	)
}

DokarkivForm.validation = {
	dokarkiv: ifPresent(
		'$dokarkiv',
		Yup.object({
			tittel: requiredString,
			tema: requiredString,
			journalfoerendeEnhet: Yup.string(),
			dokumenter: Yup.object({
				brevkode: requiredString
			})
		})
	)
}
