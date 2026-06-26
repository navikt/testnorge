import React, { createContext, ReactNode, useContext, useEffect, useMemo, useState } from 'react'
import { useDashboardFeilDetaljert, useDashboardFeilSummert } from '@/utils/hooks/useDashboard'
import {
	type FeilPeriodeOption,
	toFeilGrupper,
	toFeilPeriodeOptions,
	toFeilSummertView,
} from './dashboardFeilUtils'
import { createFeilSummertChartOptions } from './dashboardChartOptions'
import type { FeilOversikt } from '@/types/Dashboard'

interface DashboardFeilContextType {
	// Period selection
	feilYearOptions: string[]
	selectedFeilYear: string | null
	feilMonthOptions: { value: string; label: string }[]
	onSelectedFeilYearChange: (year: string) => void
	selectedFeilInterval: string
	onSelectedFeilIntervalChange: (value: string) => void
	feilPeriodeVisning: string

	// Chart & data
	feilSummertChartOptions: any
	feilDagerMedFeil: number
	feilTotalt: number
	hasFeilData: boolean

	// Loading & errors
	loadingFeilSummert: boolean
	feilSummertError: any

	// Day selection
	selectedFeilDay: number | null
	selectedFeilPunkt: any
	feilSelectedDayLabel: string
	onSelectedFeilDayChange: (day: number | null) => void

	// Error groups
	feilGrupper: any[]
	feilDetaljertCount: number
	loadingFeilDetaljert: boolean
	feilDetaljertError: any
}

const DashboardFeilContext = createContext<DashboardFeilContextType | undefined>(undefined)

interface DashboardFeilProviderProps {
	children: ReactNode
	dashboardOversikt: FeilOversikt[]
	mockModeEnabled: boolean
	mockData?: any
	makeYearChangeHandler: (
		intervalOptions: any[],
		setSetter: (value: string) => void,
	) => (year: string) => void
	buildFeilPeriodView: (options: FeilPeriodeOption[], interval: string) => any
}

export const DashboardFeilProvider: React.FC<DashboardFeilProviderProps> = ({
	children,
	dashboardOversikt,
	mockModeEnabled,
	mockData,
	makeYearChangeHandler,
	buildFeilPeriodView,
}) => {
	const [selectedFeilInterval, setSelectedFeilInterval] = useState('')
	const [selectedFeilDay, setSelectedFeilDay] = useState<number | null>(null)

	// Build period view from oversikt data
	const feilPeriodeOptions = useMemo(
		() => toFeilPeriodeOptions(dashboardOversikt),
		[dashboardOversikt],
	)

	const feilView = useMemo(
		() => buildFeilPeriodView(feilPeriodeOptions, selectedFeilInterval),
		[feilPeriodeOptions, selectedFeilInterval, buildFeilPeriodView],
	)

	const selectedFeilPeriode = useMemo(
		() => feilPeriodeOptions.find((option) => option.interval === selectedFeilInterval),
		[feilPeriodeOptions, selectedFeilInterval],
	)

	const feilYear = selectedFeilPeriode?.aar ?? null
	const feilMonthName = selectedFeilPeriode?.maanedNavn ?? null

	// Fetch feil data
	const { feilSummert, loadingFeilSummert, feilSummertError } = useDashboardFeilSummert(
		mockModeEnabled ? null : feilYear,
		mockModeEnabled ? null : feilMonthName,
	)

	const { feilDetaljert, loadingFeilDetaljert, feilDetaljertError } = useDashboardFeilDetaljert(
		mockModeEnabled ? null : feilYear,
		mockModeEnabled ? null : feilMonthName,
		mockModeEnabled ? null : selectedFeilDay,
	)

	// Switch to mock data when enabled
	const activeFeilSummert = useMemo(
		() => (mockModeEnabled ? (mockData?.feilByInterval[selectedFeilInterval] ?? []) : feilSummert),
		[mockModeEnabled, mockData, selectedFeilInterval, feilSummert],
	)

	const activeFeilDetaljert = useMemo(
		() =>
			mockModeEnabled
				? (mockData?.feilDetaljertByDate[
						selectedFeilDay !== null
							? `${selectedFeilInterval}-${String(selectedFeilDay).padStart(2, '0')}`
							: ''
					] ?? [])
				: feilDetaljert,
		[mockModeEnabled, mockData, selectedFeilInterval, selectedFeilDay, feilDetaljert],
	)

	// Build views
	const feilSummertView = useMemo(() => toFeilSummertView(activeFeilSummert), [activeFeilSummert])

	const feilSummertChartOptions = useMemo(
		() =>
			createFeilSummertChartOptions(
				feilSummertView.punkter,
				feilSummertView.fagsystemNokler,
				setSelectedFeilDay,
			),
		[feilSummertView.punkter, feilSummertView.fagsystemNokler],
	)

	const feilGrupper = useMemo(() => toFeilGrupper(activeFeilDetaljert), [activeFeilDetaljert])

	const selectedFeilPunkt = useMemo(
		() =>
			selectedFeilDay !== null
				? (feilSummertView.punkter.find((punkt) => punkt.dag === selectedFeilDay) ?? null)
				: null,
		[selectedFeilDay, feilSummertView.punkter],
	)

	// Auto-select latest interval when periode options first load
	useEffect(() => {
		if (!selectedFeilInterval && feilPeriodeOptions.length > 0) {
			const latestInterval = feilPeriodeOptions[feilPeriodeOptions.length - 1]?.interval
			if (latestInterval) {
				setSelectedFeilInterval(latestInterval)
			}
		}
	}, [feilPeriodeOptions, selectedFeilInterval])

	// Reset day selection when interval changes
	useEffect(() => {
		setSelectedFeilDay(null)
	}, [selectedFeilInterval])

	const value: DashboardFeilContextType = {
		// Period selection
		feilYearOptions: feilView.yearOptions,
		selectedFeilYear: feilView.selectedYear,
		feilMonthOptions: feilView.monthOptions,
		onSelectedFeilYearChange: makeYearChangeHandler(
			feilView.intervalOptions,
			setSelectedFeilInterval,
		),
		selectedFeilInterval,
		onSelectedFeilIntervalChange: setSelectedFeilInterval,
		feilPeriodeVisning: selectedFeilPeriode?.intervalVisning ?? '',

		// Chart & data
		feilSummertChartOptions,
		feilDagerMedFeil: feilSummertView.punkter.length,
		feilTotalt: feilSummertView.punkter.reduce((sum, punkt) => sum + punkt.total, 0),
		hasFeilData: feilSummertView.punkter.length > 0,

		// Loading & errors
		loadingFeilSummert: loadingFeilSummert && !mockModeEnabled,
		feilSummertError: mockModeEnabled ? undefined : feilSummertError,

		// Day selection
		selectedFeilDay,
		selectedFeilPunkt,
		feilSelectedDayLabel: selectedFeilPunkt?.datoVisning ?? '',
		onSelectedFeilDayChange: setSelectedFeilDay,

		// Error groups
		feilGrupper,
		feilDetaljertCount: new Set(activeFeilDetaljert.map((rad) => rad.bestillingId)).size,
		loadingFeilDetaljert: loadingFeilDetaljert && !mockModeEnabled,
		feilDetaljertError: mockModeEnabled ? undefined : feilDetaljertError,
	}

	return <DashboardFeilContext.Provider value={value}>{children}</DashboardFeilContext.Provider>
}

export const useDashboardFeil = () => {
	const context = useContext(DashboardFeilContext)
	if (!context) {
		throw new Error('useDashboardFeil must be used within DashboardFeilProvider')
	}
	return context
}
