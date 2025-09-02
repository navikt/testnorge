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
	title?: string | undefined
}

export const ArbeidsgiverIdent = ({
	path,
	isDisabled,
	title = undefined,
}: ArbeidsgiverIdentProps) => {
	const formMethods = useFormContext()
	const [personnummer, setPersonnummer] = useState(formMethods.watch(path))
	const [success, setSuccess] = useBoolean(false)
	const { result, loading: loadingNaviger, error: errorNaviger } = useNaviger(personnummer)

	useEffect(() => {
		if (personnummer) {
			if (result?.identNavigerTil) {
				formMethods.setValue(path, personnummer, { shouldTouch: true })
				formMethods.trigger(path)
				formMethods.clearErrors(`manual.${path}`)
				formMethods.clearErrors(path)
				setSuccess(true)
			} else {
				formMethods.setError(`manual.${path}`, { message: 'Fant ikke arbeidsgiver-ident' })
			}
		}
	}, [result, errorNaviger])

	const handleChange = (event: React.ChangeEvent<any>) => {
		setSuccess(false)
		const personnr = event.target.value
		formMethods.setValue(path, personnr, { shouldTouch: true })
		formMethods.clearErrors(`manual.${path}`)
		formMethods.trigger(path)
		setPersonnummer(personnr)
	}

	return (
		<div>
			<DollyTextInput
				name={path}
				// @ts-ignore
				value={personnummer}
				label={'Arbeidsgiver ident'}
				onChange={handleChange}
				isDisabled={loadingNaviger || isDisabled}
				size="xlarge"
				title={title}
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
