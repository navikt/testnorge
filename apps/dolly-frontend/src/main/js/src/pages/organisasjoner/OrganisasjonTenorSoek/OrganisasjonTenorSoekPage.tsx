import Title from '../../../components/title'
import { useTenorOversiktOrganisasjoner } from '@/utils/hooks/useTenorSoek'
import React, { Suspense, useEffect, useState } from 'react'
import { TreffListeOrg } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/TreffListeOrg'
import { SoekFormOrg } from '@/pages/organisasjoner/OrganisasjonTenorSoek/SoekFormOrg'
import styled from 'styled-components'
import Loading from '@/components/ui/loading/Loading'
import { Button } from '@navikt/ds-react'
import {
	ChevronDownDoubleIcon,
	ChevronUpDoubleIcon,
	ExclamationmarkTriangleIcon,
} from '@navikt/aksel-icons'

const NavigateButton = styled(Button)`
	position: fixed;
	bottom: 30px;
	left: 50%;
	transform: translateX(-50%);
	z-index: 1000;
	box-shadow: var(--ax-shadow-dialog);
	border-radius: var(--ax-radius-full);
	width: 12rem;

	.aksel-label {
		visibility: visible;
	}

	.aksel-loader {
		position: relative;
	}
`

const initialState = {
	organisasjonListe: [],
	side: 0,
	seed: null,
	nesteSide: null,
}

export default () => {
	const [request, setRequest] = useState({})
	const [state, setState] = useState<any>(initialState)
	const [overTreff, setOverTreff] = useState(false)
	const { response, loading, error, mutate } = useTenorOversiktOrganisasjoner(
		request,
		10,
		state.side,
		state.seed,
	)

	useEffect(() => {
		if (response) {
			const organisasjoner = response.data?.data?.organisasjoner || []
			const nextState = {
				...state,
				organisasjonListe:
					state.side > 0 ? [...state.organisasjonListe, ...organisasjoner] : organisasjoner,
				seed: response.data?.data?.seed,
				nesteSide: response.data?.data?.nesteSide,
			}
			setState(nextState)
		}
	}, [response])

	useEffect(() => {
		window.addEventListener('scroll', handleScroll)
		return () => window.removeEventListener('scroll', handleScroll)
	}, [])

	useEffect(() => {
		const soekElement = document.getElementById('soek')
		if (!soekElement || typeof ResizeObserver === 'undefined') {
			return
		}
		const resizeObserver = new ResizeObserver(() => updateButtonPosition())
		resizeObserver.observe(soekElement)
		return () => resizeObserver.disconnect()
	}, [])

	const updateButtonPosition = () => {
		const treffElement = document.getElementById('treff')
		const soekElement = document.getElementById('soek')
		if (treffElement && soekElement) {
			const buttonPosition = window.scrollY + window.innerHeight - 30
			const soekTopVisible = soekElement.getBoundingClientRect().top >= 0
			setOverTreff(!soekTopVisible && buttonPosition >= treffElement.offsetTop)
		}
	}

	const navigateTo = (element: string) => {
		const treff = document.getElementById(element)?.offsetTop
		window.scrollTo({ top: treff, behavior: 'smooth' })
	}

	const handleScroll = () => {
		updateButtonPosition()
		if (
			document.documentElement.scrollHeight - document.documentElement.scrollTop >
			document.documentElement.clientHeight
		) {
			return
		}
		setState((state: any) => {
			if (state.side < 19 && state.nesteSide) {
				return { ...state, side: state.nesteSide }
			}
			return state
		})
	}

	const ingenTreff = response?.data?.data?.treff < 1

	const getIcon = () => {
		if (ingenTreff) {
			return <ExclamationmarkTriangleIcon aria-hidden />
		} else if (overTreff) {
			return <ChevronUpDoubleIcon aria-hidden />
		} else {
			return <ChevronDownDoubleIcon aria-hidden />
		}
	}

	return (
		<Suspense fallback={<Loading label="Laster søkeside ..." panel />}>
			<div id="soek">
				<div className="flexbox--align-center--justify-start">
					<Title title="Søk etter organisasjoner i Tenor (Test-Norge)" />
				</div>
				<div className="flexbox--flex-wrap">
					<NavigateButton
						variant={loading || ingenTreff ? 'primary-neutral' : 'primary'}
						onClick={() => navigateTo(overTreff ? 'soek' : 'treff')}
						icon={loading ? null : getIcon()}
						loading={loading}
						disabled={loading || ingenTreff}
					>
						{loading
							? 'Henter treff ...'
							: ingenTreff
								? 'Ingen treff'
								: overTreff
									? 'Gå til søk'
									: 'Gå til treff'}
					</NavigateButton>
					<SoekFormOrg setRequest={setRequest} mutate={mutate} />
				</div>
			</div>
			<div id="treff">
				<TreffListeOrg
					response={response?.data}
					organisasjonListe={state.organisasjonListe}
					loading={loading}
					error={error}
				/>
			</div>
		</Suspense>
	)
}
