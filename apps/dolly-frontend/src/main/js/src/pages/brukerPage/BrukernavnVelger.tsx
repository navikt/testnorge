import React, { useState } from 'react'
import styled from 'styled-components'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Bruker, Organisasjon } from '@/pages/brukerPage/types'
import { BrukerApi } from '@/service/Api'
import { FormProvider, useForm } from 'react-hook-form'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'
import { yupResolver } from '@hookform/resolvers/yup'
import * as yup from 'yup'
import { DollyErrorMessage } from '@/utils/DollyErrorMessageWrapper'

type BrukernavnVelgerProps = {
	eksisterendeBrukernavn?: string
	organisasjon: Organisasjon
	addToSession: (org: string) => void
}

const validation = yup.object().shape({
	brukernavn: yup
		.string()
		.required('Brukernavn er påkrevd')
		.min(5, 'Brukernavn må være minst 5 tegn')
		.max(25, 'Brukernavn kan ikke være mer enn 25 tegn')
		.matches(/^[a-zA-Z0-9æøåÆØÅ ]+$/, 'Kun bokstaver og tall er tillatt'),
	epost: yup.string().email('Epost må være på gyldig format').required('Epost er påkrevd'),
})

const ButtonDiv = styled.div`
	display: flex;
	flex-direction: row;
	align-items: center;
	margin-top: 20px;
`

const Selector = styled.div`
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 20px;
`

const feilmeldinger = {
	ukjent: 'Noe gikk galt. Vennligst prøv på nytt.',
}

export default ({ eksisterendeBrukernavn, organisasjon, addToSession }: BrukernavnVelgerProps) => {
	const formMethods = useForm({
		defaultValues: {
			brukernavn: eksisterendeBrukernavn || '',
			epost: '',
		},
		resolver: yupResolver(validation),
		mode: 'onChange',
	})
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(null as string | null)

	const onSubmitUpdate = async (formData: any) => {
		setError(null)
		setLoading(true)
		BrukerApi.updateBruker(eksisterendeBrukernavn, formData.epost, organisasjon.organisasjonsnummer)
			.then((response: Bruker) => {
				if (response !== null) {
					addToSession(organisasjon.organisasjonsnummer)
				} else {
					setError(feilmeldinger.ukjent)
				}
			})
			.catch(() => setError(feilmeldinger.ukjent))
		setLoading(false)
	}

	const onSubmit = async (formData: any) => {
		setError(null)
		setLoading(true)
		BrukerApi.opprettBruker(
			formData?.brukernavn,
			formData?.epost,
			organisasjon?.organisasjonsnummer,
		)
			.then((response: Bruker) => {
				if (response !== null) {
					addToSession(organisasjon.organisasjonsnummer)
				} else {
					setError(feilmeldinger.ukjent)
				}
			})
			.catch(() => setError(feilmeldinger.ukjent))
		setLoading(false)
	}

	return (
		<FormProvider {...formMethods}>
			<form onSubmit={formMethods.handleSubmit(eksisterendeBrukernavn ? onSubmitUpdate : onSubmit)}>
				<h3>
					Fyll inn eget navn som brukes når du representerer <b>{organisasjon.navn}</b> og legg inn
					en epost du kan kontaktes på. Neste gang du logger inn for denne organisasjonen skjer det
					automatisk med denne brukeren.
				</h3>

				<Selector>
					<DollyTextInput
						name="brukernavn"
						label="Navn"
						size="large"
						defaultValue={eksisterendeBrukernavn}
						isDisabled={loading || !!eksisterendeBrukernavn}
					/>
					<DollyTextInput name="epost" label="Epost" size="large" isDisabled={loading} />
					<ButtonDiv>
						<NavButton
							type="submit"
							variant={'primary'}
							className="videre-button"
							disabled={loading}
						>
							Gå videre til Dolly
						</NavButton>
						<NavButton
							className="tilbake-button"
							onClick={() => navigateToLogin()}
							variant={'secondary'}
						>
							Tilbake til innlogging
						</NavButton>
					</ButtonDiv>
					{error && <DollyErrorMessage message={error} />}
				</Selector>
			</form>
		</FormProvider>
	)
}
