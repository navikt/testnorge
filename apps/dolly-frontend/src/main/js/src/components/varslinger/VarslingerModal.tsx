import React, { useEffect, useState } from 'react'
import { isAfter, isBefore } from 'date-fns'
import DollyModal from '~/components/ui/modal/DollyModal'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { VarslingerTekster } from './VarslingerTekster'
import './varslingerModal.less'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { updateVarslingerBruker } from '~/ducks/varslinger'
import { VarslingerApi } from '~/service/Api'
import { useBoolean } from 'react-use'
import { Stepper } from '@navikt/ds-react'
import { useDispatch } from 'react-redux'

type Varsling = {
	fom: string
	tom: string
	varslingId: string
}

export const VarslingerModal = () => {
	const [steg, setSteg] = useState(0)
	const [modalOpen, setModalOpen] = useState(true)
	const [varslinger, setVarslinger] = useState(null)
	const [varslingerBruker, setVarslingerbruker] = useState(null)
	const dispatch = useDispatch()

	const [isLoadingVarslinger, setIsLoadingVarslinger] = useBoolean(true)
	const [isLoadingVarslingerBruker, setIsLoadingVarslingerBruker] = useBoolean(true)

	const isLoading = isLoadingVarslinger || isLoadingVarslingerBruker

	useEffect(() => {
		VarslingerApi.getVarslinger().then((response: { data: Varsling }) => {
			setVarslinger(response.data)
			setIsLoadingVarslinger(false)
		})
		VarslingerApi.getVarslingerBruker().then((response: { data: Varsling }) => {
			setVarslingerbruker(response.data)
			setIsLoadingVarslingerBruker(false)
		})
	}, [])

	if (!varslinger) {
		return null
	}

	const usetteVarslinger =
		isLoading === false &&
		varslinger.length > 0 &&
		varslinger.filter((varsel: Varsling) => !varslingerBruker.includes(varsel.varslingId))
	if (!usetteVarslinger || usetteVarslinger.length < 1) {
		return null
	}

	const currentDate = new Date()
	const gyldigeVarslinger = usetteVarslinger.filter(
		(varsling: Varsling) =>
			(!varsling.fom && (!varsling.tom || isBefore(currentDate, new Date(varsling.tom)))) ||
			(!varsling.tom && (!varsling.fom || isAfter(currentDate, new Date(varsling.fom)))) ||
			(isAfter(currentDate, new Date(varsling.fom)) &&
				isBefore(currentDate, new Date(varsling.tom)))
	)

	const antallVarslinger = gyldigeVarslinger.length
	if (antallVarslinger < 1) {
		return null
	}

	const varslingerSteg = gyldigeVarslinger.map((varsling: Varsling) => ({
		label: varsling.varslingId,
	}))

	const submitSettVarsling = (siste: boolean) => {
		siste ? setModalOpen(false) : setSteg(steg + 1)
		dispatch(updateVarslingerBruker(gyldigeVarslinger[steg].varslingId))
	}

	return (
		<ErrorBoundary>
			<DollyModal isOpen={modalOpen} noCloseButton={true} width="70%" overflow="auto">
				<div className="varslinger-modal">
					{antallVarslinger > 1 && <Stepper activeStep={steg}>{varslingerSteg}</Stepper>}

					<VarslingerTekster varslingId={gyldigeVarslinger[steg].varslingId} />

					<div className="varslinger-buttons">
						{steg > 0 && (
							<NavButton onClick={() => setSteg(steg - 1)} style={{ float: 'left' }}>
								Forrige side
							</NavButton>
						)}
						{steg < antallVarslinger - 1 ? (
							<NavButton
								variant={'primary'}
								onClick={() => submitSettVarsling(false)}
								style={{ float: 'right' }}
							>
								Neste side
							</NavButton>
						) : (
							<NavButton
								variant={'primary'}
								onClick={() => submitSettVarsling(true)}
								style={{ float: 'right' }}
							>
								Lukk
							</NavButton>
						)}
					</div>
				</div>
			</DollyModal>
		</ErrorBoundary>
	)
}
