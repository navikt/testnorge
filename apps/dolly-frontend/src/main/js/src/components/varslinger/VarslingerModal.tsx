import React, { useState } from 'react'
import { isAfter, isBefore } from 'date-fns'
import DollyModal from '~/components/ui/modal/DollyModal'
import Stegindikator from 'nav-frontend-stegindikator'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { VarslingerTekster } from './VarslingerTekster'
import './varslingerModal.less'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface Varslinger {
	varslinger: Array<Varsling>
	varslingerBruker: Array<string>
	isLoadingVarslinger: boolean
	updateVarslingerBruker: Function
}

type Varsling = {
	fom: string
	tom: string
	varslingId: string
}

export const VarslingerModal = ({
	varslinger,
	varslingerBruker,
	isLoadingVarslinger,
	updateVarslingerBruker
}: Varslinger) => {
	const [steg, setSteg] = useState(0)
	const [modalOpen, setModalOpen] = useState(true)

	if (!varslinger) return null

	const usetteVarslinger =
		isLoadingVarslinger === false &&
		varslinger.length > 0 &&
		varslinger.filter(varsel => !varslingerBruker.includes(varsel.varslingId))
	if (!usetteVarslinger || usetteVarslinger.length < 1) return null

	const currentDate = new Date()
	const gyldigeVarslinger = usetteVarslinger.filter(
		varsling =>
			(!varsling.fom && (!varsling.tom || isBefore(currentDate, new Date(varsling.tom)))) ||
			(!varsling.tom && (!varsling.fom || isAfter(currentDate, new Date(varsling.fom)))) ||
			(isAfter(currentDate, new Date(varsling.fom)) &&
				isBefore(currentDate, new Date(varsling.tom)))
	)

	const antallVarslinger = gyldigeVarslinger.length
	if (antallVarslinger < 1) return null

	const varslingerSteg = gyldigeVarslinger.map(varsling => ({ label: varsling.varslingId }))

	const submitSettVarsling = (siste: boolean) => {
		siste ? setModalOpen(false) : setSteg(steg + 1)
		updateVarslingerBruker(gyldigeVarslinger[steg].varslingId)
	}

	return (
		<ErrorBoundary>
			<DollyModal isOpen={modalOpen} noCloseButton={true} width="70%" overflow="auto">
				<div className="varslinger-modal">
					{/* 
                //@ts-ignore */}
					{antallVarslinger > 1 && (
						<Stegindikator aktivtSteg={steg} steg={varslingerSteg} kompakt />
					)}

					<VarslingerTekster varslingId={gyldigeVarslinger[steg].varslingId} />

					<div className="varslinger-buttons">
						{steg > 0 && (
							<NavButton
								type="standard"
								onClick={() => setSteg(steg - 1)}
								style={{ float: 'left' }}
							>
								Forrige side
							</NavButton>
						)}
						{steg < antallVarslinger - 1 ? (
							<NavButton
								type="hoved"
								onClick={() => submitSettVarsling(false)}
								style={{ float: 'right' }}
							>
								Neste side
							</NavButton>
						) : (
							<NavButton
								type="hoved"
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
