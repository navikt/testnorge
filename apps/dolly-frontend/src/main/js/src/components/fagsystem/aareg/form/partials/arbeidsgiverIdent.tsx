import React, { useEffect, useState } from 'react'
import { useBoolean } from 'react-use'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { useFormContext } from 'react-hook-form'
import { useNaviger } from '@/utils/hooks/useNaviger'

type ArbeidsgiverIdentProps = {
	path: string
	isDisabled?: boolean
}

export const ArbeidsgiverIdent = ({ path, isDisabled }: ArbeidsgiverIdentProps) => {
	const formMethods = useFormContext()
	const [personnummer, setPersonnummer] = useState(formMethods.watch(path))
	const [success, setSuccess] = useBoolean(false)
	const { result, loading: loadingNaviger, error: errorNaviger } = useNaviger(personnummer)

	useEffect(() => {
		if (personnummer) {
			if (result?.identNavigerTil) {
				setSuccess(true)
				formMethods.clearErrors(path)
			} else {
				formMethods.setError(path, { message: 'Fant ikke arbeidsgiver-ident' })
			}
		}
	}, [result, errorNaviger])

	const handleChange = (event: React.ChangeEvent<any>) => {
		formMethods.clearErrors(path)
		setSuccess(false)
		const personnr = event.target.value
		formMethods.setValue(`${path}`, personnr)
		if (personnr.match(/^\d{11}$/) != null) {
			setPersonnummer(personnr)
		} else {
			formMethods.setError(path, {
				message: 'Ident må være et tall med 11 siffer',
			})
		}
	}

	return (
		<div>
			<DollyTextInput
				name={path}
				// @ts-ignore
				defaultValue={personnummer}
				label={'Arbeidsgiver ident'}
				onBlur={handleChange}
				isDisabled={loadingNaviger || isDisabled}
			/>
			{success && (
				<div className="flexbox" style={{ marginTop: '-5px' }}>
					<Icon size={22} kind="feedback-check-circle" style={{ marginRight: '10px' }} /> Ident
					funnet
				</div>
			)}
			{loadingNaviger && <Loading label="Sjekker arbeidsgiver-ident" />}
		</div>
	)
}
