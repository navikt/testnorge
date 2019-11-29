import React, { useState, useEffect } from 'react'
import _get from 'lodash/get'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { DollyApi } from '~/service/Api'

export const Diskresjonskoder = ({ formikBag }) => {
	const [diskresjonskode1, setDiskresjonskode1] = useState(
		formikBag.values.tpsf.utenFastBopel
			? 'UFB'
			: formikBag.values.tpsf.spesreg
			? formikBag.values.tpsf.spesreg
			: ''
	)
	const [diskresjonskode2, setDiskresjonskode2] = useState(
		formikBag.values.tpsf.utenFastBopel
			? formikBag.values.tpsf.spesreg
				? formikBag.values.tpsf.spesreg
				: ''
			: ''
	)
	const [harEkstraDiskresjonskode, setHarEkstraDiskresjonskode] = useState(
		formikBag.values.tpsf.utenFastBopel ? true : false
	)
	const [options, setOptions] = useState([])
	const harBoadresse = Boolean('boadresse' in formikBag.initialValues.tpsf)

	// Henter options for ekstra diskresjonskode, som ikke kan inneholde UFB og SPSF
	useEffect(() => {
		const fetchData = async () => {
			const res = await DollyApi.getKodeverkByNavn('Diskresjonskoder')
			setOptions(DollyApi.Utils.NormalizeKodeverkForDropdownUtenUfb(res, true).options)
		}
		fetchData()
	}, [])

	// Endrer form og felter ved endringer
	const handleChangeDiskresjonskode1 = val => {
		setDiskresjonskode1(val)
		formikBag.setFieldValue('tpsf.spesreg', val)
		if (val !== 'UFB') {
			if (formikBag.values.tpsf.utenFastBopel) {
				formikBag.setFieldValue('tpsf.utenFastBopel', false)
				setDiskresjonskode2('')
				setHarEkstraDiskresjonskode(false)
			}
			if (_get(formikBag, 'values.tpsf.boadresse.kommunenr') && !harBoadresse) {
				formikBag.setFieldValue('tpsf.boadresse', null)
			}
		}
	}

	const handleChangeDiskresjonskode2 = val => {
		setDiskresjonskode2(val)
		formikBag.setFieldValue('tpsf.utenFastBopel', true)
		formikBag.setFieldValue('tpsf.spesreg', val)
	}

	const handleRemoveButton = () => {
		setHarEkstraDiskresjonskode(false)
		formikBag.setFieldValue('tpsf.spesreg', diskresjonskode1)
		formikBag.setFieldValue('tpsf.utenFastBopel', false)
		setDiskresjonskode2('')
	}

	const handleChangeKommunenr = val => {
		val && formikBag.setFieldValue('tpsf.boadresse.adressetype', 'GATE')
	}

	return (
		<React.Fragment>
			<DollySelect
				name="diskresjonskode"
				label="Diskresjonskode"
				kodeverk="Diskresjonskoder"
				size="large"
				value={diskresjonskode1}
				onChange={v => handleChangeDiskresjonskode1(v.value)}
				isClearable={false}
			/>
			{diskresjonskode1 === 'UFB' && (
				<React.Fragment>
					{!harEkstraDiskresjonskode && (
						<FieldArrayAddButton
							title="Legg til diskresjonskode"
							onClick={() => setHarEkstraDiskresjonskode(true)}
						/>
					)}
					{harEkstraDiskresjonskode && (
						<div className="flexbox">
							<DollySelect
								name="ekstraDiskresjonskode"
								label="Ekstra diskresjonskode"
								options={options}
								size="large"
								value={diskresjonskode2}
								onChange={v => handleChangeDiskresjonskode2(v.value)}
								isClearable={false}
							/>
							<FieldArrayRemoveButton onClick={handleRemoveButton} />
						</div>
					)}
					{!harBoadresse && (
						<FormikSelect
							name="tpsf.boadresse.kommunenr"
							label="Kommunenummer"
							afterChange={v => handleChangeKommunenr(v.value)}
							kodeverk="Kommuner"
						/>
					)}
				</React.Fragment>
			)}
		</React.Fragment>
	)
}
