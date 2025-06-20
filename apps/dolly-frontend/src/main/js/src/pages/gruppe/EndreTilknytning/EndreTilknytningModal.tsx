import { DollyModal } from '@/components/ui/modal/DollyModal'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import React, { useState } from 'react'
import { useAlleBrukere } from '@/utils/hooks/useBruker'
import { DollyApi } from '@/service/Api'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import styled from 'styled-components'

const Knappegruppe = styled.div`
	margin-top: 20px;
	display: flex;
	gap: 15px;
`

export const EndreTilknytningModal = ({ gruppe, modalIsOpen, closeModal }) => {
	const { brukere, loading: loadingBrukere } = useAlleBrukere()
	const [valgtBruker, setValgtBruker] = useState(gruppe.opprettetAv?.brukerId)
	const [feilmelding, setFeilmelding] = useState('')

	const brukerOptions = brukere?.map((bruker) => {
		const erTeam = bruker?.brukertype === 'TEAM'
		return {
			value: bruker?.brukerId,
			label: bruker?.brukernavn + (erTeam ? ' (team)' : ''),
		}
	})

	const matchMutate = useMatchMutate()

	const handleSubmit = () => {
		setFeilmelding('')
		DollyApi.endreTilknytningGruppe(gruppe.id, valgtBruker).then(() => {
			matchMutate(REGEX_BACKEND_GRUPPER)
				.then(() => closeModal())
				.catch((error) => {
					setFeilmelding('Feil ved endring av eier: ' + error.message)
				})
		})
	}

	return (
		<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
			<>
				<h1>
					Bytt eier for gruppe #{gruppe.id} - {gruppe.navn}
				</h1>
				<div style={{ marginTop: '15px' }}>
					<DollySelect
						name="bruker"
						label="Ny eier"
						options={brukerOptions ?? []}
						value={valgtBruker}
						size="grow"
						onChange={(value) => setValgtBruker(value?.value)}
						isLoading={loadingBrukere}
						placeholder={loadingBrukere ? 'Laster brukere ...' : 'Velg bruker ...'}
						isClearable={false}
					/>
				</div>
				{feilmelding && (
					<div role="alert" aria-live="assertive">
						<div className="skjemaelement__feilmelding">{feilmelding}</div>
					</div>
				)}
				<Knappegruppe>
					<NavButton
						variant="danger"
						onClick={() => {
							setFeilmelding('')
							closeModal()
						}}
					>
						Avbryt
					</NavButton>
					<NavButton onClick={handleSubmit} variant="primary">
						Bytt eier
					</NavButton>
				</Knappegruppe>
			</>
		</DollyModal>
	)
}
