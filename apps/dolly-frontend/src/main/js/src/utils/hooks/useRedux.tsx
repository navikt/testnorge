import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux'
import { AppDispatch, RootState } from '@/Store'

// Use throughout your app instead of plain `useDispatch` and `useSelector`
type DispatchFunc = () => AppDispatch
export const useReduxDispatch: DispatchFunc = useDispatch
export const useReduxSelector: TypedUseSelectorHook<RootState> = useSelector
