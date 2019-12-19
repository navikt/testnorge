import React from 'react'
import { useState } from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import Knapp from 'nav-frontend-knapper'

export const Postadresser = ({ formikBag }) => {
	const [postLinje1, setPostLinje1] = useState('')
	const [postLinje2, setPostLinje2] = useState('')
	const [postLinje3, setPostLinje3] = useState('')

	const endrePostlinje1 = value => {
		setPostLinje1(value)
		formikBag.setFieldValue('tpsf.postadresse[0].postLinje1', value)
	}
	const endrePostlinje2 = value => {
		setPostLinje2(value)
		if (!formikBag.values.tpsf.postadresse[0].postLinje1) {
			formikBag.setFieldValue('tpsf.postadresse[0].postLinje1', value)
		} else {
			formikBag.setFieldValue('tpsf.postadresse[0].postLinje2', value)
		}
	}
	const endrePostlinje3 = selected => {
		console.log('selected :', selected)
		setPostLinje3(selected)
		if (!formikBag.values.tpsf.postadresse[0].postLinje1) {
			formikBag.setFieldValue('tpsf.postadresse[0].postLinje1', selected)
		} else if (!formikBag.values.tpsf.postadresse[0].postLinje2) {
			formikBag.setFieldValue('tpsf.postadresse[0].postLinje2', selected)
		} else {
			formikBag.setFieldValue('tpsf.postadresse[0].postLinje3', selected)
		}
	}

	return (
		<Kategori title="Postadresse" vis="tpsf.postadresse">
			<FormikSelect name="tpsf.postadresse[0].postLand" label="Land" kodeverk="Landkoder" />
			{formikBag.values.tpsf.postadresse[0].postLand !== 'NOR' && (
				<React.Fragment>
					<FormikTextInput name="tpsf.postadresse[0].postLinje1" label="Postlinje 1" />
					<FormikTextInput name="tpsf.postadresse[0].postLinje2" label="Postlinje 2" />
					<FormikTextInput name="tpsf.postadresse[0].postLinje3" label="Postlinje 3" />
				</React.Fragment>
			)}

			{formikBag.values.tpsf.postadresse[0].postLand === 'NOR' && (
				<React.Fragment>
					<DollyTextInput
						name="postLinje1"
						id="p1"
						label="Postlinje 1"
						size="grow"
						value={postLinje1}
						label="Postlinje 1"
						onChange={v => endrePostlinje1(v.target.value)}
					/>
					<DollyTextInput
						name="postLinje2"
						id="p2"
						label="Postlinje 2"
						size="grow"
						value={postLinje2}
						label="Postlinje 2"
						onChange={v => endrePostlinje2(v.target.value)}
					/>
					<FormikSelect
						name="postLinje3"
						label="Postlinje 3"
						id="p3"
						kodeverk="Postnummer"
						size="grow"
						value={postLinje3}
						onChange={selected => endrePostlinje3(selected.label)}
					/>
				</React.Fragment>
			)}
		</Kategori>
	)
}
